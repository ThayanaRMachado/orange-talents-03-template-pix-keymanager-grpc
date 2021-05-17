package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoConta as TipoContaRecebida
import br.com.zup.edu.TipoChave as TipoChaveRecebida
import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import br.com.zup.edu.cadastro.ChavePix
import br.com.zup.edu.cadastro.compartilhado.ValidaIdUnico
import br.com.zup.edu.cadastro.compartilhado.validacao.ValidaChavePix
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.NotBlank

@Introspected
@ValidaChavePix
class NovaChave(
    @field:ValidaIdUnico
    @field:NotNull
    val clienteId: String,

    @field:NotNull
    val tipo: TipoChaveRecebida,

    @field:NotBlank
    val valor: String,

    @field:NotNull
    val tipoConta: TipoContaRecebida

) {
    fun toModel(): ChavePix = ChavePix(
        clienteId = UUID.fromString(clienteId),
        tipoChave = TipoChave.valueOf(tipo!!.name),
        valorChave = if (tipo == TipoChaveRecebida.ALEATORIA){
            UUID.randomUUID().toString()
        } else {
            valor!!
        },
        tipoConta = TipoConta.valueOf(tipoConta!!.name)
    )
}