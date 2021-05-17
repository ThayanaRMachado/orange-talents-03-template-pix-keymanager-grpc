package br.com.zup.edu.cadastro.compartilhado.exception

import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExceptioHandlerResolver(
    @Inject private val handlers: List<ExceptionHandler<Exception>>
) {
    private var defaultHandler: ExceptionHandler<Exception> = DefaultExceptionHandler()

    constructor(handlers: List<ExceptionHandler<Exception>>, defaultHandler: ExceptionHandler<Exception>): this(handlers) {
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception): ExceptionHandler<Exception> {
        val foundHandlers = handlers.filter { it.supports((e)) }
        if (foundHandlers.size > 1){
            throw IllegalStateException("Muitos handlers suportando a mesma exceção")
        }
        return foundHandlers.firstOrNull() ?: defaultHandler
    }

}
