package br.com.zup.edu.deleta

import br.com.zup.edu.*
import br.com.zup.edu.cadastro.ChavePix
import br.com.zup.edu.cadastro.ContasDeClientesNoItauClient
import br.com.zup.edu.cadastro.RegistraChaveEndpointTest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeletaChavePixEndpointTest(
    val grpcClient: KeyManagerDeletaGrpcServiceGrpc.KeyManagerDeletaGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var repository: ChavePixRepository

    @field:Inject
    lateinit var itau: ContasDeClientesNoItauClient

    companion object {
        val idItau = UUID.randomUUID().toString()
    }

    lateinit var chave_existente: ChavePix

    @BeforeEach
    fun setup() {
        chave_existente = repository.save(
            ChavePix(
                TipoDeChave.CPF,
                "02467781054",
                idItau,
                br.com.zup.edu.TipoDeConta.CONTA_CORRENTE,
            )
        )
    }

    @AfterEach
    fun cleanup() {
        repository.deleteAll()
    }

    @Test
    fun `deve remover chave pix existente`() {

        Mockito.`when`(itau.retornaDadosCliente(RegistraChaveEndpointTest.idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok())

        val response: DeletaChavePixResponse = grpcClient.deleta(
            DeletaChavePixRequest.newBuilder()
                .setPixId(chave_existente.id)
                .setIdTitular(chave_existente.idTitular)
                .build()
        )

        with(response) {
            assertFalse(repository.existsById(chave_existente.id))
        }

    }

    @Test
    fun `nao deve remover chave pix quando inexistente`(){

        Mockito.`when`(itau.retornaDadosCliente(RegistraChaveEndpointTest.idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        //val pixIdInexistente = UUID.randomUUID().toString()
        val pixIdInexistente = idItau

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(
                DeletaChavePixRequest.newBuilder()
                .setPixId(pixIdInexistente)
                .setIdTitular(chave_existente.idTitular)
                .build())
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada.", status.description)
        }
    }

    @Test
    fun `nao deve remover chave pix quando chave existente mas pertence a outro cliente`() {

        Mockito.`when`(itau.retornaDadosCliente(RegistraChaveEndpointTest.idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val outroClienteId = UUID.randomUUID().toString()

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(
                DeletaChavePixRequest.newBuilder()
                    .setPixId(chave_existente.id)
                    .setIdTitular(outroClienteId)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada.", status.description)
        }
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerDeletaGrpcServiceGrpc.KeyManagerDeletaGrpcServiceBlockingStub {
            return KeyManagerDeletaGrpcServiceGrpc.newBlockingStub(channel)

        }
    }

    @MockBean(ContasDeClientesNoItauClient::class)
    fun itaumock(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

}