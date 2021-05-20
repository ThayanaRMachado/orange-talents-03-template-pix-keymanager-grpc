package br.com.zup.edu.compartilhados.validacao

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Constraint(validatedBy = [ChavePixValidator::class])
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidaChavePix(
    val message: String = "O formato da chave \${validatedValue.tipoDeChave} é inválido!",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)