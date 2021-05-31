package br.com.zup.edu.cadastro

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
class ContaAssociada(
    @field:NotBlank
    val instituicao: String,

    @field:NotBlank
    val nomeTitular: String,

    @field:NotBlank
    @field:Size(max = 11)
    val cpfTitular: String,

    @field:NotBlank
    @Column(nullable = false)
    val agencia: String,

    @field:NotBlank
    @Column(nullable = false)
    val numero: String
) {
}