package io.github.mdsimmo.cmdmsg

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.LOCAL_VARIABLE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Msg(val name: String, val description: String = "")