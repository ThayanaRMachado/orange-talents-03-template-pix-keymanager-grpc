package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import br.com.zup.edu.bcb.*
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeChave: TipoDeChave,

    @field:NotBlank
    @Column(nullable = false, unique = true)
    var valor: String,

    @field:NotNull
    val idTitular: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeConta: TipoDeConta,

    @Column(nullable = false)
    @field:Embedded
    val conta: ContaAssociada
) {

    @Id
    var id: String = UUID.randomUUID().toString()

    fun chaveAleatoria(key: String): ChavePix {
        this.valor = key
        return ChavePix(this.tipoDeChave, valor, this.idTitular,this.tipoDeConta, this.conta)
    }

    fun toBancoCentralRequest(): BancoCentralRequest {
        return BancoCentralRequest(
            keyType = KeyType.by(this.tipoDeChave),
            key = this.valor,
            BankAccountRequest(
                participant = "60701190",
                branch = this.conta.agencia,
                accountNumber = this.conta.numero,
                accountType = AccountTypeEnum.by(this.tipoDeConta)
            ),
            OwnerRequest(
                type = Type.NATURAL_PERSON,
                name = this.conta.nomeTitular,
                taxIdNumber = this.conta.cpfTitular
            )
        )
    }
}