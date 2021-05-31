package br.com.zup.edu.bcb

import br.com.zup.edu.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:8082")
interface BancoCentralClient {

    @Post("/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun createChavePixBcb(@Body request: BancoCentralRequest): HttpResponse<BancoCentralResponse>

    @Delete("/api/v1/pix/keys/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun deleteChavePixBcb(@PathVariable key: String, @Body request: DeleteChavePixRequest): HttpResponse<Any>
}
