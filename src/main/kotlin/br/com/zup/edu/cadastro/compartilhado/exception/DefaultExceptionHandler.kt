package br.com.zup.edu.cadastro.compartilhado.exception

import javax.inject.Singleton
import br.com.zup.edu.cadastro.compartilhado.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import java.lang.Exception

@Singleton
class DefaultExceptionHandler: ExceptionHandler<Exception> {

    override fun handle(e: Exception): StatusWithDetails =
        ExceptionHandler.StatusWithDetails(
            Status.UNKNOWN
                .withDescription(e.message)
                .withCause(e)
        )

    override fun supports(e: Exception): Boolean = false
}