package br.com.zup.edu.cadastro

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChaveRepository: JpaRepository<ChavePix, Long> {
    fun findByValorChave(valorChave: String): Optional<ChavePix>

    fun existsByValorChave(valorChave: String): Boolean
}