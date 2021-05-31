package br.com.zup.edu.cadastro

import br.com.zup.edu.*
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.bcb.*
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
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub,
) {

    @field:Inject
    lateinit var itau: ContasDeClientesNoItauClient

    @field:Inject
    lateinit var bcb: BancoCentralClient

    companion object {
        val idItau = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova chave`() {

        `when`(itau.validaCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse))

        `when`(bcb.createChavePixBcb(bcbRequest))
            .thenReturn(HttpResponse.created(bcbResponse))

        val response: RegistraChavePixResponse = grpcClient.registra(
            RegistraChavePixRequest.newBuilder()
                .setIdTitular(idItau)
                .setTipoDeChave(TipoDeChave.CPF)
                .setValor("02467781054")
                .setTipoDeConta(br.com.zup.edu.TipoDeConta.CONTA_CORRENTE)
                .build()
        )

        with(response) {
            assertEquals(idItau, idTitular)
            assertNotNull(pixId)
        }

    }

    @Test
    fun `nao deve registrar uma nova chave pix quando ja foi cadastrada antes`() {

        `when`(itau.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse))

        val chavePix = ChavePix(
            TipoDeChave.CPF,
            "02457781054",
            idItau,
            TipoDeConta.CONTA_CORRENTE,
            dadosDaContaResponse.toModel()
        )

        repository.save(chavePix)

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setIdTitular(idItau)
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setValor("02467781054")
                    .setTipoDeConta(br.com.zup.edu.TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertTrue(this.message!!.contains("Chave Pix já cadastrada!"))
        }
    }

    @Test
    fun `nao deve registrar uma nova chave pix quando os parametros forem invalidos`() {

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setIdTitular("")
                    .setTipoDeChave(TipoDeChave.UNKNOWN_TIPO_CHAVE)
                    .setValor("")
                    .setTipoDeConta(br.com.zup.edu.TipoDeConta.UNKNOWN_TIPO_CONTA)
                    .build()
            )
        }

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)
        assertTrue(response.message!!.contains("Valores inválidos"))

    }

    @Test
    fun `nao deve registrar uma nova chave quando nao encontrar dados da conta do cliente`() {
        `when`(itau.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(
                RegistraChavePixRequest.newBuilder()
                    .setIdTitular(idItau)
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setValor("02467781054")
                    .setTipoDeConta(br.com.zup.edu.TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)
        assertTrue(response.message!!.contains("Cliente inexistente"))
    }

    val dadosDaContaResponse =
        DadosDaContaResponse(
            tipoDeConta = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "02467781054")
        )

    val bcbRequest = BancoCentralRequest(
        keyType = KeyType.by(TipoDeChave.CPF),
        key = "5585987654322",
        bankAccount = BankAccountRequest(
            participant = "60701190",
            branch = "0001",
            accountNumber = "201900",
            accountType = AccountTypeEnum.CACC
        ),
        owner = OwnerRequest(
            type = Type.NATURAL_PERSON,
            name = "Rafael M C Ponte",
            taxIdNumber = "63657520235"
        )
    )

    private val bcbResponse = BancoCentralResponse(
        key = "02467781054",
        criadoEm = LocalDateTime.now()
    )

    @Factory
    class Clients {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub {
            return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)

        }
    }

    @MockBean(ContasDeClientesNoItauClient::class)
    fun validaCliente(): ContasDeClientesNoItauClient {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @MockBean(BancoCentralClient::class)
    fun createChavePixBcb(): BancoCentralClient? {
        return Mockito.mock(BancoCentralClient::class.java)
    }
}
