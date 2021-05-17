package br.com.zup.edu.cadastro.client

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class ContaResponse(
    @JsonProperty("tipo")
    val tipo: String,

    @JsonProperty("instituicao")
    val instituicao: InstituicaoResponse,

    @JsonProperty("agencia")
    val agencia: String,

    @JsonProperty("numero")
    val numero: String,

    @JsonProperty("titular")
    val titular: TitularResponse
)

class InstituicaoResponse(
    @JsonProperty("nome")
    val nome: String,

    @JsonProperty("ispb")
    val ispb: String
)

class TitularResponse(
    @JsonProperty("id")
    val id: UUID,

    @JsonProperty("nome")
    val nome: String,

    @JsonProperty("cpf")
    val cpf: String
)
