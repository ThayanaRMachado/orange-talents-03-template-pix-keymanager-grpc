package br.com.zup.edu.cadastro

import br.com.zup.edu.ChavePixRepository
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.bcb.BancoCentralClient
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
    @Inject val bcbClient: BancoCentralClient
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix {

        if (repository.existsByValor(novaChave.valor))
            throw IllegalArgumentException("Chave Pix '${novaChave}' existente")

        val response = itauClient.retornaDadosCliente(novaChave.idTitular, novaChave.tipoDeConta.name)

        val conta = response.body()!!.toModel()

        val chavePix = novaChave.toModel(conta)

        val bcbResponse = bcbClient.createChavePixBcb(chavePix.toBancoCentralRequest())

        if (bcbResponse.status != HttpStatus.CREATED) {
            throw IllegalStateException("Erro ao registrar a chave Pix no Banco Central do Brasil")
        }

        if (chavePix.tipoDeChave == TipoDeChave.ALEATORIA) {
            chavePix.chaveAleatoria(bcbResponse.body()!!.key)
        }

        return repository.save(chavePix)
            .also {
                logger.info("Chave Pix de ID ${chavePix.id} criada")
            }
    }
}