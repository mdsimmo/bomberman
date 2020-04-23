package io.github.mdsimmo.cmdmsg

import java.lang.annotation.Repeatable

@Suppress("DEPRECATED_JAVA_ANNOTATION")
@Repeatable(Args::class)
@kotlin.annotation.Repeatable
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.LOCAL_VARIABLE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Arg(val name: String, val description: String = "")


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Args(vararg val value: Arg)