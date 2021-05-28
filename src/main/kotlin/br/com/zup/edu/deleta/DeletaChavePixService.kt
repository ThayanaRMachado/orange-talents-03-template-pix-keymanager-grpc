package br.com.zup.edu.deleta

import br.com.zup.edu.ChavePixRepository
import br.com.zup.edu.compartilhados.handlers.ChavePixInexistenteException
import io.grpc.Status
import io.grpc.stub.ClientResponseObserver
import io.grpc.stub.StreamObserver
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Validated
@Singleton
class DeletaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository
) {
    private val logger = LoggerFactory.getLogger(DeletaChavePixService::class.java)

    @Transactional
    fun buscaChaveDelete(pixId: String, idTitular: String) {

        val chaveInformada =
            chavePixRepository.findByIdAndIdTitular(pixId, idTitular)
                .orElseThrow {
                    ChavePixInexistenteException("A chave informada não existe ou não pertence ao cliente.")
                }

        chavePixRepository.delete(chaveInformada)

                logger.info("Chave Pix de ID ${pixId} deletada")

    }

}