package br.com.zup.edu.compartilhados.validacao

import br.com.zup.edu.compartilhados.validacao.ChavePixValidator
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass

@Constraint(validatedBy = [])
@MustBeDocumented
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Pattern(regexp = "^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}\$",
message = "O ID informado não está no formato UUID")
annotation class ValidaUUID(
    val message: String = "O ID informado não está no formato UUID",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)