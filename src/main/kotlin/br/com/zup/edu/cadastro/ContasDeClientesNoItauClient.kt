package br.com.zup.edu.cadastro

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import io.micronaut.retry.annotation.Retryable

@Client("http://localhost:9091")
interface ContasDeClientesNoItauClient {

    @Get("/api/v1/clientes/{clientId}/contas")
    fun retornaDadosCliente(@PathVariable clientId: String, @QueryValue tipo: String):
            HttpResponse<DadosDaContaResponse>

    @Retryable
    fun validaCliente(
        @PathVariable("clienteId") identificadorItau: String, @QueryValue("tipo")
        tipo: String
    ): HttpResponse<DadosDaContaResponse>
}