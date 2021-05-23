package br.com.zup.edu.cadastro

import br.com.zup.edu.*
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub,
    val repository: ChavePixRepository,
    val client: ContasDeClientesNoItauClient
) {

    @Test
    fun `deve salvar uma chave pix com cpf`() {

        // repository.deleteAll()

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoDeChave(TipoDeChave.CPF)
                .setValor("02467781054")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
            assertTrue(repository.existsById(pixId))
        }
    }

    @Test
    fun `deve salvar a chave pix com email`() {
        repository.deleteAll()

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setValor("joao@email.com")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
            assertTrue(repository.existsById(pixId))
        }
    }

    @Test
    fun `deve salvar a chave pix com celular`() {
        //repository.deleteAll()

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoDeChave(TipoDeChave.CELULAR)
                .setValor("+5531988888888")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
            assertTrue(repository.existsById(pixId))
        }
    }

    @Test
    fun `deve salvar a chave pix aleatoria`() {
        repository.deleteAll()

        val response = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular(UUID.randomUUID().toString())
                .setTipoDeChave(TipoDeChave.ALEATORIA)
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(pixId)
            assertTrue(repository.existsById(pixId))
        }
    }

    @Test
    fun `nao deve adicionar uma nova chave quando já existir uma cadastrada`() {
        val existente =
            repository.save(
                ChavePix(
                    TipoDeChave.CPF,
                    "86135457004",
                    "5260263c-a3c1-4727-ae32-3bdb2538841b",
                    TipoDeConta.CONTA_CORRENTE
                )
            )

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setTipoDeChave(existente.tipoDeChave)
                    .setValor(existente.valor)
                    .setIdTitular(existente.idTitular)
                    .setTipoDeConta(existente.tipoDeConta)
                    .build()
            )
        }

        with(erro) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix já cadastrado!", status.description)
        }
    }

    @MockBean(ContasDeClientesNoItauClient::class)
    fun erpClient(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)

        }

    }
}