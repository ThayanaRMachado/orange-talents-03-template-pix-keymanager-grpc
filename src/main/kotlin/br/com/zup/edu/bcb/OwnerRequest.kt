package br.com.zup.edu.bcb

data class OwnerRequest(
    val type: Type,
    val name: String,
    val taxIdNumber: String
) {
}