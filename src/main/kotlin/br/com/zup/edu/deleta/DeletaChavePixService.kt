package br.com.zup.edu.deleta

import br.com.zup.edu.ChavePixRepository
import br.com.zup.edu.compartilhados.handlers.ChavePixNaoExistenteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Validated
@Singleton
class DeletaChavePixService(@Inject val chavePixRepository: ChavePixRepository
) {
    private val logger = LoggerFactory.getLogger(DeletaChavePixService::class.java)

    @Transactional
    fun buscaChaveDelete(pixId: String, idTitular: String) {
        logger.info("Procurando chave informada")

        val chaveInformada =
            chavePixRepository.findByIdAndIdTitular(pixId, idTitular)
                .orElseThrow {
                    ChavePixNaoExistenteException("A chave informada não existe ou não pertence ao cliente.")
                }
        logger.info("Busca concluída")

        chavePixRepository.delete(chaveInformada)
    }
}