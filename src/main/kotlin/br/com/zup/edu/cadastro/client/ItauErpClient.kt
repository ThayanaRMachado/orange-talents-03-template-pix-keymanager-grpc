package br.com.zup.edu.cadastro.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client
import java.net.http.HttpResponse

@Client("http://localhost:9091")
interface ItauErpClient {
    @Get("/api/v1/clientes/{clientId}/contas")
    fun getAccount(@PathVariable clientId: String, @QueryValue("tipo") type: String): HttpResponse<ContaResponse>
}