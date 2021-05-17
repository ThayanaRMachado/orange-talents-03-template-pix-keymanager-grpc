package br.com.zup.edu.cadastro.compartilhado.exception

import javax.inject.Singleton
import br.com.zup.edu.cadastro.compartilhado.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import java.lang.Exception

@Singleton
class ValidationExceptionHandler: ExceptionHandler<ValidationException> {

    override fun handle(e: ValidationException): StatusWithDetails =
        StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )

    override fun supports(e: Exception): Boolean = e is ValidationException

}