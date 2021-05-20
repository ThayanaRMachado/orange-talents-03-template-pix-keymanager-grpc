package br.com.zup.edu.compartilhados.handlers

import com.google.rpc.Code
import com.google.rpc.Status
import javax.inject.Singleton

@Singleton
class RecursoNaoEncontradoExceptionHandler: ExceptionHandler<RecursoNaoEncontradoException> {

    override fun handle(exception: RecursoNaoEncontradoException) =
        Status.newBuilder().setCode(Code.NOT_FOUND_VALUE)
            .setMessage(exception.message)
            .build()

    override fun supports(exception: Exception) = exception is RecursoNaoEncontradoException
}