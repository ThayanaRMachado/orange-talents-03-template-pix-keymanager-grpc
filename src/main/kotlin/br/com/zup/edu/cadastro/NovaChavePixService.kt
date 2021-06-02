package br.com.zup.edu.cadastro

import br.com.zup.edu.ChavePixRepository
import br.com.zup.edu.bcb.BancoCentralClient
import br.com.zup.edu.bcb.CreatePixKeyRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.grpc.annotation.GrpcService
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val repository: ChavePixRepository,
    @Inject val itauClient: ContasDeClientesNoItauClient,
    @Inject val bcbClient: BancoCentralClient,
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix{

        if (repository.existsByValor(novaChave.valor))
            throw IllegalArgumentException("Chave Pix '${novaChave.valor}' existente")

        val response = itauClient.retornaDadosCliente(novaChave.idTitular, novaChave.tipoDeConta.name)
        val conta = response.body().toModel()

        val chavePix = novaChave.toModel(conta)
        return repository.save(chavePix)
            .also {
                logger.info("Chave Pix de ID ${chavePix.id} criada")
            }

        val bcbRequest = CreatePixKeyRequest.of(chavePix).also {
            logger.info("Registrando chave Pix no Banco Central do Brasil (BCB): $it")
        }

        val bcbResponse = bcbClient.create(bcbRequest)
        if (bcbResponse.status != HttpStatus.CREATED)
            throw IllegalStateException("Erro ao registrar chave Pix no Banco Central do Brasil (BCB)")

        // 5. atualiza chave do dominio com chave gerada pelo BCB
        if (chavePix.isAleatoria()) {
            chavePix.valor = bcbResponse.body()!!.key
        }

        return chavePix
    }
}