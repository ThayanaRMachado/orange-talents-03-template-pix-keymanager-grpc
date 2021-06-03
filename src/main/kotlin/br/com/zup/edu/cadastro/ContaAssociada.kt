package br.com.zup.edu.cadastro

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class ContaAssociada(

    @field:NotBlank
    @Column(name = "conta_instituicao", nullable = false)
    val instituicao: String,

    @field:NotBlank
    @Column(name = "conta_titular_nome", nullable = false)
    val nomeDoTitular: String,

    @field:NotBlank
    @field:Size(max = 11)
    @Column(name = "conta_titular_cpf", length = 11, nullable = false)
    val cpfDoTitular: String,

    @Column(nullable = false)
    val agencia: String,

    @Column(nullable = false)
    val numero: String
) {
    companion object {
        public val ITAU_UNIBANCO_ISPB: String = "60701190"
    }
}