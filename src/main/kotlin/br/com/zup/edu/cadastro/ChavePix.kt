package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Entity
class ChavePix(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeChave: TipoDeChave,

    @Column(nullable = false, unique = true)
    var valor: String,

    @field:NotNull
    val idTitular: String = UUID.randomUUID().toString(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeConta,

    @field:Valid
    @Embedded
    val conta: ContaAssociada

    ) {

    @Id
    var id: String = UUID.randomUUID().toString()

    @Column(nullable = false)
    val criadaEm: LocalDateTime = LocalDateTime.now()

    fun pertenceAo(clienteId: UUID) = this.idTitular.equals(clienteId)

    fun isAleatoria(): Boolean {
        return tipoDeChave == TipoDeChave.ALEATORIA
    }
}