package br.com.zup.edu.cadastro

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class ContaAssociada(
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipo: TipoDeConta,

    @Column(nullable = false)
    val agencia: String,

    @Column(nullable = false)
    val numero: String
) {
}