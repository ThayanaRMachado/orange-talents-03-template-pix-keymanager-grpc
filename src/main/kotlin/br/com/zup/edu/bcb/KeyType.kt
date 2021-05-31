package br.com.zup.edu.bcb

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.TipoDeChave.*

enum class KeyType {
    CPF,
    PHONE,
    EMAIL,
    RANDOM;

    companion object {
        fun by(tipoChaveEnum: TipoDeChave): KeyType {
            return when (tipoChaveEnum) {
                TipoDeChave.CPF -> CPF
                CELULAR -> PHONE
                TipoDeChave.EMAIL -> EMAIL
                ALEATORIA -> RANDOM
                UNKNOWN_TIPO_CHAVE -> TODO()
                UNRECOGNIZED -> TODO()
            }
        }
    }
}