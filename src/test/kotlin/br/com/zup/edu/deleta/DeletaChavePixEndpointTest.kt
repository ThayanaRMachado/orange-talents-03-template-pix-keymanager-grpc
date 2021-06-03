package br.com.zup.edu.deleta

import br.com.zup.edu.*
import br.com.zup.edu.bcb.BancoCentralClient
import br.com.zup.edu.bcb.DeletePixKeyRequest
import br.com.zup.edu.bcb.DeletePixKeyResponse
import br.com.zup.edu.cadastro.ChavePix
import br.com.zup.edu.cadastro.ContaAssociada
import br.com.zup.edu.cadastro.ContasDeClientesNoItauClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
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

    @field:Inject
    lateinit var bcb: BancoCentralClient

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
                "c56dfef4-7901-44fb-84e2-a2cefb157890",
                br.com.zup.edu.TipoDeConta.CONTA_CORRENTE,
                conta = ContaAssociada(
                    instituicao = "UNIBANCO ITAU SA", "60701190",
                    agencia = "0001",
                    cpfDoTitular = "02467781054",
                    numero = "0001"
                )
            )
        )
    }

    @AfterEach
    fun cleanup() {
        repository.deleteAll()
    }

    @Test
    fun `deve remover chave pix existente`() {

        Mockito.`when`(itau.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val response: DeletaChavePixResponse = grpcClient.deleta(
            DeletaChavePixRequest.newBuilder()
                .setPixId(chave_existente.id)
                .setIdTitular(chave_existente.idTitular)
                .build()
        )

        with(response) {
            assertFalse(repository.existsByValor(valor = "rafael@email.com"))
        }

    }


    @Test
    fun `nao deve remover chave pix quando inexistente`() {
        Mockito.`when`(itau.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(
                DeletaChavePixRequest.newBuilder()
                    .setPixId(idItau)
                    .setIdTitular(chave_existente.idTitular)
                    .build()
            )
        }

        assertEquals(Status.NOT_FOUND.code, response.status.code)
    }

    @Test
    fun `nao deve remover chave pix quando chave existente mas pertence a outro cliente`() {

        Mockito.`when`(itau.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
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


    fun deletaChavePixRequest(): DeletePixKeyRequest {
        return DeletePixKeyRequest(
            key = "60701190",
            participant = "rafael@email.com"
        )
    }

    private fun deletaChavePixResponse(): DeletePixKeyResponse {
        return DeletePixKeyResponse(
            key = "rafael@email.com",
            participant = "60701190",
            deletedAt = LocalDateTime.now()
        )
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

    @MockBean(BancoCentralClient::class)
    fun createChavePixBcb(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
    }

}