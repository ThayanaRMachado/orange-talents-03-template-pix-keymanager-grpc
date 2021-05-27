package br.com.zup.edu.cadastro

import br.com.zup.edu.ChavePixRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.grpc.annotation.GrpcService
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

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix{

        val response = itauClient.retornaDadosCliente(novaChave.idTitular, novaChave.tipoDeConta.name)
        val conta = response.body().toModel()

        val chavePix = novaChave.toModel(conta)
        return repository.save(chavePix)
            .also {
                logger.info("Chave Pix de ID ${chavePix.id} criada")
            }
    }
}