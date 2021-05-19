package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.compartilhados.validacao.UUID
import br.com.zup.edu.compartilhados.validacao.ValidaChavePix
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidaChavePix
@Introspected
data class NovaChavePix(

    @field:UUID
    @field:NotBlank
    val idTitular: String,

    @field:NotNull
    val tipoDeChave: TipoDeChave,

    @field:Size(max = 77)
    val valor: String?,

    @field:NotNull
    val tipoDeConta: TipoDeConta
) {

    fun toModel(contaAssociada: ContaAssociada): ChavePix {

        return ChavePix(tipoDeChave, valor!!, idTitular)
    }
}
