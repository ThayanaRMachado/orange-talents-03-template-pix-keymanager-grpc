package br.com.zup.edu.cadastro

import br.com.zup.edu.*
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.bcb.*
import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.HttpResponseException
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
    lateinit var itauClient: ContasDeClientesNoItauClient

    @Inject
    lateinit var bcbClient: BancoCentralClient

    companion object {
        val idItau = UUID.randomUUID().toString()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma nova chave pix`() {

        `when`(itauClient.validaCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse))

          `when`(bcbClient.create(createPixKeyRequest()))
              .thenReturn(HttpResponse.created(createPixKeyResponse()))
         val response = grpcClient.registra(
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

        repository.save(
            ChavePix(
                TipoDeChave.CPF,
                "02467781054",
                idItau,
                br.com.zup.edu.TipoDeConta.CONTA_CORRENTE,
                conta = ContaAssociada(
                    instituicao = "UNIBANCO ITAU",
                    nomeDoTitular = "Rafael M C Ponte",
                    cpfDoTitular = "02467781054",
                    agencia = "0001",
                    numero = "291900"
                )
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
            assertEquals("Chave Pix já cadastrada!", status.description)
        }
    }

    @Test
    fun `nao deve registrar chave pix quando nao encontrar dados da conta cliente`() {

        `when`(itauClient.validaCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setIdTitular(idItau)
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setValor("rponte@gmail.com")
                .setTipoDeConta(br.com.zup.edu.TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        assertEquals(Status.INVALID_ARGUMENT.code, response.status.code)
        assertTrue(response.message!!.contains("Cliente inexistente"))
    }

    @Test
    fun `nao deve registrar chave pix quando nao for possivel registrar chave no BCB`() {
        `when`(itauClient.retornaDadosCliente(idItau, "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse))

        `when`(bcbClient.create(createPixKeyRequest()))
            .thenReturn(HttpResponse.badRequest())

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder()
                .setIdTitular(idItau)
                .setTipoDeChave(TipoDeChave.CPF)
                .setValor("02467781054")
                .setTipoDeConta(br.com.zup.edu.TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Erro ao registrar chave Pix no Banco Central do Brasil (BCB)", status.description)
        }
    }

    @Test
    fun `nao deve registrar uma nova chave pix quando os parametros forem invalidos`() {

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registra(RegistraChavePixRequest.newBuilder().build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    private val dadosDaContaResponse =
        DadosDaContaResponse(
            tipoDeConta = TipoDeConta.CONTA_CORRENTE,
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse("Rafael Ponte", "02467781054")
        )

    private fun createPixKeyRequest(): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = PixKeyType.CPF,
            key = "02467781054",
            bankAccount = BankAccount(
                participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
                branch = "0001",
                accountNumber = "291900",
                accountType = BankAccount.AccountType.CACC
            ),
            owner = owner()
        )
    }

    private fun createPixKeyResponse(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            keyType = PixKeyType.CPF,
            key = "02467781054",
            bankAccount = bankAccount(),
            owner = owner(),
            createdAt = LocalDateTime.now()
        )
    }

    private fun bankAccount(): BankAccount {
        return BankAccount(
            participant = ContaAssociada.ITAU_UNIBANCO_ISPB,
            branch = "1218",
            accountNumber = "291900",
            accountType = BankAccount.AccountType.CACC
        )
    }

    private fun owner(): Owner {
        return Owner(
            type = Owner.OwnerType.NATURAL_PERSON,
            name = "Rafael Ponte",
            taxIdNumber = "63657520325"
        )
    }

    private fun chave(
        tipo: br.com.zup.edu.TipoDeChave,
        chave: String = UUID.randomUUID().toString(),
        idTitular: String = UUID.randomUUID().toString()
    ): ChavePix {
        return ChavePix(
            idTitular = idTitular,
            tipoDeChave = tipo,
            valor = chave,
            tipoDeConta = br.com.zup.edu.TipoDeConta.CONTA_CORRENTE,
            conta = ContaAssociada(
                instituicao = "UNIBANCO ITAU",
                nomeDoTitular = "Rafael Ponte",
                cpfDoTitular = "63657520325",
                agencia = "1218",
                numero = "291900"
            )
        )
    }

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