package br.com.zup.edu.cadastro.compartilhado.exception

import javax.inject.Singleton
import br.com.zup.edu.cadastro.compartilhado.exception.ExceptionHandler
import br.com.zup.edu.cadastro.compartilhado.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status

@Singleton
class NotFoundExceptionHandler: ExceptionHandler<NotFoundException> {

    override fun handle(e: NotFoundException): StatusWithDetails =
       StatusWithDetails(
           Status.NOT_FOUND
               .withDescription(e.message)
               .withCause(e)
       )

    override fun supports(e: Exception): Boolean = e is NotFoundException
}