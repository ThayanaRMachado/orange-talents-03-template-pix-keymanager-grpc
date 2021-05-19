package br.com.zup.edu.cadastro

import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
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
            throw java.lang.IllegalStateException("Chave Pix ${novaChave.valor} existente")

        val response = itauClient.retornaDadosCliente(novaChave.idTitular, novaChave.tipoDeConta.name)
        val conta = response.body().toModel() ?: throw HttpStatusException(HttpStatus.NOT_FOUND, "Cliente não localizado")

        val chavePix = novaChave.toModel(conta)
        return repository.save(chavePix)
            .also {
                LOGGER.info("Chave Pix de ID ${chavePix.id} criada")
            }
    }
}
