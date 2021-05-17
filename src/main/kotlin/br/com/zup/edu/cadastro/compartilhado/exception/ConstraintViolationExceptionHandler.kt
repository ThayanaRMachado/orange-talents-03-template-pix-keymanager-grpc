package br.com.zup.edu.cadastro.compartilhado.exception

import br.com.zup.edu.cadastro.compartilhado.exception.ExceptionHandler
import br.com.zup.edu.cadastro.compartilhado.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import java.lang.Exception
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ConstraintViolationExceptionHandler: ExceptionHandler<ConstraintViolationException> {

    override fun handle(e: ConstraintViolationException): StatusWithDetails =
        ExceptionHandler.StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )

    override fun supports(e: Exception): Boolean = e is ConstraintViolationException
}