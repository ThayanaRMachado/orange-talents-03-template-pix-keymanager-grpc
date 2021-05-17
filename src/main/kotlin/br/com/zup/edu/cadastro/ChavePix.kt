package br.com.zup.edu.cadastro

import br.com.zup.edu.TipoChave
import br.com.zup.edu.TipoConta
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.math.max

@Entity
class ChavePix(
    @field:NotNull
    val clienteId: UUID,

    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    val tipoChave: TipoChave,

    @field:NotBlank
    @field:Size(max = 77)
    val valorChave: String = UUID.randomUUID().toString(),

    @field:NotNull
    @field:Enumerated(EnumType.STRING)
    val tipoConta: TipoConta
) {
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}