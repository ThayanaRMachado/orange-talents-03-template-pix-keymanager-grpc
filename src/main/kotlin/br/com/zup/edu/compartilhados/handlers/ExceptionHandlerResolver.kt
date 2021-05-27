package br.com.zup.edu.compartilhados.handlers

import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptionHandlerResolver(
    @Inject private val handlers: List<ExceptionHandler<*>>) {

    private var defaultHandler: ExceptionHandler<*> = DefaultExceptionHandler()

    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>) : this(handlers) {
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception): ExceptionHandler<*> {
        val foundHandlers = handlers.filter { h -> h.supports(e) }
        if (foundHandlers.size > 1)
            throw IllegalStateException("Too many handlers supporting the same exception '${e.javaClass.name}': $foundHandlers")

        return foundHandlers.firstOrNull() ?: defaultHandler
    }

    /*private val logger = LoggerFactory.getLogger(this::class.java)

    fun resolve(e: Exception): ExceptionHandler<*> {
        val foundHandlers: List<ExceptionHandler<*>> = listaHandlers.filter {
            it.supports(e) }

        if (foundHandlers.size > 1) {
            throw IllegalArgumentException("Há mais de um handler para a mesma exception.")
        }

        return foundHandlers.firstOrNull()?
    }*/
}