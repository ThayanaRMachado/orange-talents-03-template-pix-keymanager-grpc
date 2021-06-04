package br.com.zup.edu.carrega

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.cadastro.ChavePix
import br.com.zup.edu.cadastro.ContaAssociada
import java.time.LocalDateTime
import java.util.*

data class ChavePixInfo(
    val pixId: String = UUID.randomUUID().toString(),
    val idTitular: String = UUID.randomUUID().toString(),
    val tipo: TipoDeChave,
    val valor: String,
    val tipoDeConta: TipoDeConta,
    val conta: ContaAssociada,
    val registradaEm: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun of(chave: ChavePix): ChavePixInfo {
            return ChavePixInfo(
                pixId = chave.id,
                idTitular = chave.idTitular,
                tipo = chave.tipoDeChave,
                valor = chave.valor,
                tipoDeConta = chave.tipoDeConta,
                conta = chave.conta,
                registradaEm = chave.criadaEm
            )
        }
    }
}
