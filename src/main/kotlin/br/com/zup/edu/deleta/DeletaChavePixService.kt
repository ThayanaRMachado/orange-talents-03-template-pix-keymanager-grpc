package br.com.zup.edu.deleta

import br.com.zup.edu.ChavePixRepository
import br.com.zup.edu.bcb.BancoCentralClient
import br.com.zup.edu.bcb.DeleteChavePixRequest
import br.com.zup.edu.compartilhados.handlers.ChavePixInexistenteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Validated
@Singleton
class DeletaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val bcbClient: BancoCentralClient
) {
    private val logger = LoggerFactory.getLogger(DeletaChavePixService::class.java)

    @Transactional
    fun buscaChaveDelete(pixId: String, idTitular: String) {

        val chaveInformada =
            chavePixRepository.findByIdAndIdTitular(pixId, idTitular)
                .orElseThrow {
                    ChavePixInexistenteException("A chave informada não existe ou não pertence ao cliente.")
                }

        bcbClient.deleteChavePixBcb(
            key = chaveInformada.valor, DeleteChavePixRequest(key = chaveInformada.valor, participant = "60701190")
        )
            .body() ?: throw IllegalStateException("Ocorreu algum erro.").also { logger.error("Erro na requisição") }

        chavePixRepository.delete(chaveInformada)
        logger.info("Chave Pix de ID ${pixId} deletada")

    }

}