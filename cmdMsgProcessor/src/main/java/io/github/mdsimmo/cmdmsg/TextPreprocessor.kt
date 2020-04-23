package io.github.mdsimmo.cmdmsg

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import io.github.mdsimmo.bomberman.messaging.Expander
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.Message
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.*
import javax.lang.model.type.MirroredTypeException
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes("io.github.mdsimmo.cmdmsg.Msg")
class TextPreprocessor : AbstractProcessor() {

    lateinit var messager: Messager
    lateinit var filer: Filer

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.messager = processingEnv.messager
        this.filer = processingEnv.filer
    }

    override fun process(annotations: Set<TypeElement?>, roundEnv: RoundEnvironment): Boolean {
        val clazzMap = mutableMapOf<TypeElement, TypeSpec.Builder>()
        for (annotatedElement in roundEnv.getElementsAnnotatedWith(Msg::class.java)) {
            if (annotatedElement.kind != ElementKind.FIELD) {
                return error(annotatedElement, "Msg must be on fields or methods")
            }
            val variable = annotatedElement as VariableElement
            var enclosingElement = variable.enclosingElement
            while (enclosingElement !is TypeElement && enclosingElement != null) {
                enclosingElement = enclosingElement.enclosingElement
            }
            val enclosingClazz = enclosingElement as TypeElement
            val defaultString = variable.constantValue
            if (defaultString !is String)
                return error(annotatedElement, "Must be of type string")
            val msg = annotatedElement.getAnnotation(Msg::class.java)
            val args = annotatedElement.getAnnotationsByType(Arg::class.java)

            val method = buildMethod(variable.simpleName.toString(), defaultString, msg, args.toList())
            val clazz = clazzMap.getOrPut(enclosingClazz) {
                TypeSpec.classBuilder(enclosingClazz.simpleName.toString() + "Texts")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            }
            clazz.addMethod(method)
        }

        clazzMap.forEach{(clazz, builder) ->
            val packageName = processingEnv.elementUtils.getPackageOf(clazz).qualifiedName.toString()
            JavaFile.builder(packageName, builder.build())
                    .build().writeTo(filer)
        }
        return true
    }

    private fun buildMethod(name: String, default: String, msg: Msg, args: List<Arg>): MethodSpec {
        val method = MethodSpec.methodBuilder(name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Message::class.java)
        for (arg in args) {
            try {
                method.addParameter(arg.type.java, arg.name)
            } catch (e: MirroredTypeException) {
                val typeMirror = e.typeMirror
                method.addParameter(TypeName.get(typeMirror), arg.name)
            }
        }
        val mapType = ParameterizedTypeName.get(Map::class.java, String::class.java, Formattable::class.java)
        method.addStatement("\$T map = new \$T()", mapType, HashMap::class.java)
        for (arg in args) {
            method.addStatement("map.put(\$S, \$L)", arg.name, arg.name)
        }
        method.addStatement("return \$T.expand(\$S, map)", Expander::class.java, default)
        return method.build()
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private fun error(element: Element, msg: String, vararg args: Any): Boolean{
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                msg.format(args),
                element)
        return true
    }
}