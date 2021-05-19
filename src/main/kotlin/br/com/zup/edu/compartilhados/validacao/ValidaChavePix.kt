package br.com.zup.edu.compartilhados.validacao

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Constraint(validatedBy = [ChavePixValidator::class])
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidaChavePix(
    val message: String = "Chave \${validatedValue.tipoDeChave} inválida!",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)