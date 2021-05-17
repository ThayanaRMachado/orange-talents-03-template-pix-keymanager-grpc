package br.com.zup.edu.cadastro

import br.com.zup.edu.cadastro.client.ItauErpClient
import br.com.zup.edu.cadastro.TipoChave
import br.com.zup.edu.cadastro.compartilhado.exception.AlreadyExistsException
import br.com.zup.edu.cadastro.compartilhado.exception.NotFoundException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.ValidationException

@Validated
@Singleton
class NovaChaveService(
    @Inject private val chaveRepository: ChaveRepository,
    @Inject private val erpClient: ItauErpClient
) {
    private val logger = LoggerFactory.getLogger(NovaChaveService::class.java)

    @Transactional
    fun salva(@Valid novaChave: NovaChave): ChavePix =
        with(novaChave) {
            if (chaveRepository.existsByValorChave(valor)) {
                throw AlreadyExistsException("Chave $valor já registrada anteriormente.")
            }

            val body = erpClient.getAccount(clienteId, tipoConta.name).body()
                ?: throw NotFoundException("Id da conta $clienteId e tipo $tipoConta.nome não encontrados")

            if (tipo == br.com.zup.edu.TipoChave.CPF && body.titular.cpf != valor) {
                throw ValidationException("CPF não pertence ao usuário informado.")
            }

            toModel()
        }.apply {
            chaveRepository.save(this)
            logger.info("Chave Pix registrada com sucesso!")
        }

}