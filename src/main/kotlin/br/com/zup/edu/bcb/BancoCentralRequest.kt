package br.com.zup.edu.bcb

data class BancoCentralRequest(
    val keyType: KeyType,
    val key: String,
    val bankAccount: BankAccountRequest,
    val owner: OwnerRequest
) {

}
