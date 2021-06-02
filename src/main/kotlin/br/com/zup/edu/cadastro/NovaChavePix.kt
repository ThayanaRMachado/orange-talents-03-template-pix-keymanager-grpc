package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.compartilhados.validacao.ValidaChavePix
import br.com.zup.edu.compartilhados.validacao.ValidaUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidaChavePix
@Introspected
data class NovaChavePix(

    @field:ValidaUUID
    @field:NotBlank
    val idTitular: String,

    @field:NotNull
    val tipoDeChave: TipoDeChave,

    @field:Size(max = 77)
    val valor: String,

    @field:NotNull
    val tipoDeConta: TipoDeConta
) {

    fun toModel(contaAssociada: ContaAssociada): ChavePix {

        return ChavePix(
            tipoDeChave = tipoDeChave,
            valor = if (!tipoDeChave.equals(TipoDeChave.ALEATORIA)) this.valor
            else UUID.randomUUID().toString(),
            idTitular = idTitular,
            tipoDeConta = tipoDeConta,
            conta = ContaAssociada(
                nomeDoTitular = contaAssociada.nomeDoTitular,
                cpfDoTitular = contaAssociada.cpfDoTitular,
                agencia = contaAssociada.agencia,
                numero = contaAssociada.numero
            )
        )
    }
}