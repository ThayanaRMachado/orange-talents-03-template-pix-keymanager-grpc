package br.com.zup.edu.cadastro

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
open class DadosDaContaResponse(

    @field: JsonProperty("tipo")
    private val tipoDeConta: TipoDeConta,

    @field: JsonProperty("instituicao")
    val instituicao: InstituicaoResponse,

    @field: JsonProperty("agencia")
    val agencia: String,

    @field: JsonProperty("numero")
    val numero: String,

    @field: JsonProperty("titular")
    val titular: TitularResponse
) {

    fun toModel(): ContaAssociada {
        return ContaAssociada(numero = numero,agencia = agencia, nomeDoTitular = titular.nome, cpfDoTitular = titular.cpf)
    }
}

class TitularResponse(
    @field: JsonProperty("nome")
    val nome: String,
    @field: JsonProperty("cpf")
    val cpf: String)
class InstituicaoResponse(
    @field: JsonProperty("nome")
    val nome: String,
    @field: JsonProperty("ispb")
    val ispb: String)