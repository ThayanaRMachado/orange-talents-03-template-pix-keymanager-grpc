package br.com.zup.edu.cadastro

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, String> {

    fun existsByValor(valor: String): Boolean
}