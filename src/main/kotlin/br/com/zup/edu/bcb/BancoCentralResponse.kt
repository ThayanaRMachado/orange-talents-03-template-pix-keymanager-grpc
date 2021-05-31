package br.com.zup.edu.bcb

import java.time.LocalDateTime

data class BancoCentralResponse(
    val key: String,
    val criadoEm: LocalDateTime
) {

}
