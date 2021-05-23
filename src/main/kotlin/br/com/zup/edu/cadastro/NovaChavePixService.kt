package br.com.zup.edu.cadastro

import br.com.zup.edu.compartilhados.handlers.ChavePixExistenteException
import br.com.zup.edu.compartilhados.handlers.RecursoNaoEncontradoException
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
    @Inject val itauClient: ContasDeClientesNoItauClient
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix{

        if (repository.existsByValor(novaChave.valor!!))
            throw ChavePixExistenteException("Chave Pix ${novaChave.valor} existente")

        val response = itauClient.retornaDadosCliente(novaChave.idTitular, novaChave.tipoDeConta.name)
        val conta = response.body().toModel() ?: throw RecursoNaoEncontradoException("Cliente inexistente ou não possui conta do tipo ${novaChave.tipoDeConta}")

        val chavePix = novaChave.toModel(conta)
        return repository.save(chavePix)
            .also {
                LOGGER.info("Chave Pix de ID ${chavePix.id} criada")
            }
    }
}