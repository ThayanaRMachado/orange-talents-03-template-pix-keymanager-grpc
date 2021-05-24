package br.com.zup.edu.cadastro

import br.com.zup.edu.KeyManagerRegistraGrpcServiceGrpc
import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub
) {

    @field:Inject
    lateinit var itau: ContasDeClientesNoItauClient

    companion object {
        val TITULAR_ID = UUID.randomUUID()
    }

    @Test
    fun `deve registrar uma chave pix`() {
        Mockito.`when`(itau.retornaDadosCliente(TITULAR_ID.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular(TITULAR_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setValor("02467781054")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

         with(response) {
            assertEquals(TITULAR_ID.toString(), idTitular)
            assertNotNull(pixId)
        }
    }

    @Test
    fun `nao deve registrar uma nova chave pix quando ja foi cadastrada antes`(){

        Mockito.`when`(itau.retornaDadosCliente(TITULAR_ID.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val chavePix = ChavePix(TipoDeChave.CPF, valor = "02467781054", idTitular = TITULAR_ID.toString(), TipoDeConta.CONTA_CORRENTE)
        repository.save(chavePix)

        val resultado = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                .setIdTitular(TITULAR_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setValor("02467781054")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        with(resultado) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
        }
    }

    @Test
    fun `deve salvar a chave pix aleatoria`() {

        Mockito.`when`(itau.retornaDadosCliente(TITULAR_ID.toString(), "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular(TITULAR_ID.toString())
                .setTipoDeChave(TipoDeChave.ALEATORIA)
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
            assertTrue(repository.existsById(pixId))
        }
    }


    @MockBean(ContasDeClientesNoItauClient::class)
    fun itaumock(): ContasDeClientesNoItauClient {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)

        }
    }

    private fun dadosDaContaResponse(): DadosDaContaResponse {
        return DadosDaContaResponse(
            tipoDeConta = br.com.zup.edu.cadastro.TipoDeConta.CONTA_CORRENTE,
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "02467781054")
        )
    }

}







