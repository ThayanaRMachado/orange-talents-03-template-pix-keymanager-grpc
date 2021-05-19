package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoDeChave
import java.util.*
import javax.persistence.*

@Entity
class ChavePix(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tipoDeChave: TipoDeChave,
    @Column(nullable = false, unique = true)
    val valor: String,
    @Column(nullable = false)
    val idTitular: String

) {

    @Id
    var id: String = UUID.randomUUID().toString()
}