package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.compartilhados.validacao.ValidaChavePix
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidaChavePix
class NovaChavePixResponse(
    @field:NotBlank
    val idTitular: String,
    @field:NotNull
    val tipoChave: TipoDeChave,
    @field:Size(max = 77)
    val valorChave: String,
    @field:NotNull
    val tipoConta: TipoDeConta
) {
    fun toModel(contaValidada: ContaAssociada): ChavePix {
        return ChavePix(
            idTitular = this.idTitular,
            tipoDeChave = TipoDeChave.valueOf(this.tipoChave!!.name),
            valor = if (this.tipoChave == TipoDeChave.ALEATORIA) "" else this.valorChave!!,
            tipoDeConta = TipoDeConta.valueOf(this.tipoConta!!.name),
            conta = contaValidada
        )
    }
}