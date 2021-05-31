package br.com.zup.edu.bcb

class BankAccountRequest(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountTypeEnum
) {
}