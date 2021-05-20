package br.com.zup.edu.compartilhados.handlers

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(CLASS, TYPE)
@Type(ExceptionHandlerInterceptor::class)
@Around
annotation class ErrorHandler {
}