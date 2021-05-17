package br.com.zup.edu.cadastro.compartilhado.exception

import br.com.zup.edu.cadastro.compartilhado.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class AlreadyExistsExceptionHandler : ExceptionHandler<AlreadyExistsException> {

    override fun handle(e: AlreadyExistsException): StatusWithDetails =
        StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )

    override fun supports(e: Exception): Boolean = e is AlreadyExistsException
}