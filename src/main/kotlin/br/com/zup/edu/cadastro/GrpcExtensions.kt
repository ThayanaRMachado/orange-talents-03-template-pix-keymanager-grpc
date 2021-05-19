package br.com.zup.edu.cadastro

import br.com.zup.edu.RegistraChavePixRequest
import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeConta

fun RegistraChavePixRequest.toModel(): NovaChavePix {
    return NovaChavePix(
        idTitular = idTitular,
        tipoDeChave = TipoDeChave.valueOf(tipoDeChave.name),
        valor = valor,
        tipoDeConta = TipoDeConta.valueOf(tipoDeConta.name)
    )
}