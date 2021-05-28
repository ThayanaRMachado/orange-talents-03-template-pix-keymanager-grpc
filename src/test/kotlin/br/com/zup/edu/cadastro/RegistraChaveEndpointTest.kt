package br.com.zup.edu.cadastro

import br.com.zup.edu.*
import br.com.zup.edu.TipoDeChave
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
       val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub,
) {
    @field:Inject
    lateinit var repository: ChavePixRepository

    /*@field:Inject
    lateinit var itau: ContasDeClientesNoItauClient*/

    companion object {
        val idItau = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun exemplo() {

    }
   /* @Test
    fun `deve cadastrar uma nova chave`() {

        `when`(itau.retornaDadosCliente(idItau,"CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse))

        val response: RegistraChavePixResponse = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular(idItau)
                .setTipoDeChave(TipoDeChave.CPF)
                .setValor("02467781054")
                .setTipoDeConta(br.com.zup.edu.TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertNotNull(this.pixId)
            assertTrue(repository.existsByValor(this.pixId))
        }

    }

    @Test
    fun `nao deve registrar uma nova chave pix quando ja foi cadastrada antes`() {

        Mockito.`when`(itau.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())


        val atual = repository.save(
            ChavePix(
                TipoDeChave.CPF,
                "02467781054",
                idItau,
                br.com.zup.edu.TipoDeConta.CONTA_CORRENTE
            )
        )

        val resultado = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setIdTitular(idItau)
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setValor("02467781054")
                    .setTipoDeConta(br.com.zup.edu.TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(resultado) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertTrue(this.message!!.contains("Chave Pix já cadastrada!"))
        }
    }

    @Test
    fun `nao deve registrar uma nova chave pix quando os parametros forem invalidos`() {

        Mockito.`when`(itau.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.badRequest())


        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setTipoDeChave(TipoDeChave.UNKNOWN_TIPO_CHAVE)
                    .setValor("")
                    .setTipoDeConta(br.com.zup.edu.TipoDeConta.UNKNOWN_TIPO_CONTA)
                    .build()
            )
        }

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)
        assertTrue(!repository.existsByValor(""))
    }
*/
    private val dadosDaContaResponse =
        DadosDaContaResponse(
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "02467781054")
        )

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)

        }
    }

    @MockBean(ContasDeClientesNoItauClient::class)
    fun itaumock(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

}