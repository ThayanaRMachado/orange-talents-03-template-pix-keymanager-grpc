package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
class ChavePix(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeChave: TipoDeChave,
    @Column(nullable = false, unique = true)
    val valor: String,
    @field:NotNull
    val idTitular: String = UUID.randomUUID().toString(),
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeConta: TipoDeConta,

) {

    @Id
    var id: String = UUID.randomUUID().toString()
}