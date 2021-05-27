package br.com.zup.edu.compartilhados.handlers

import io.grpc.Status
import br.com.zup.edu.compartilhados.handlers.ExceptionHandler.StatusWithDetails
import javax.validation.ConstraintViolationException


class DefaultExceptionHandler: ExceptionHandler<Exception> {

    override fun handle(e: Exception): StatusWithDetails {
        val status = when(e) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
            is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is ClienteInexistenteException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            else -> Status.UNKNOWN
        }
        return StatusWithDetails(status.withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return true
    }
}