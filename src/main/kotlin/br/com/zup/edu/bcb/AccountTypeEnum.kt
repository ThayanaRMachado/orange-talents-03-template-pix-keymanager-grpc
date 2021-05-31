package br.com.zup.edu.bcb

import br.com.zup.edu.TipoDeConta

enum class AccountTypeEnum {
    CACC,
    SVGS;

    companion object {
        fun by(tipoContaEnum: TipoDeConta): AccountTypeEnum {
            return when (tipoContaEnum) {
                TipoDeConta.CONTA_CORRENTE -> CACC
                else -> SVGS
            }
        }
    }
}
