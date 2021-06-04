package br.com.zup.edu

import br.com.zup.edu.cadastro.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository: JpaRepository<ChavePix, String> {

    fun existsByValor(valor: String): Boolean
    fun findByIdAndIdTitular(id: String, idTitular: String): Optional<ChavePix>
    fun findByValor(valor: String): Optional<ChavePix>
}