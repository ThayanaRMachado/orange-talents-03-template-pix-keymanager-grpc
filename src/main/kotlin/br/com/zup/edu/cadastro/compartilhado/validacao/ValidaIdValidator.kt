package br.com.zup.edu.cadastro.compartilhado

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload

import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@MustBeDocumented
@Target(FIELD, CONSTRUCTOR, PROPERTY, VALUE_PARAMETER)
@Constraint(validatedBy = [ValidaIdValidator::class])
annotation class ValidaIdUnico(
    val message: String = "O Id tem um formato inválido.",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidaIdValidator: ConstraintValidator<ValidaIdUnico, String> {

    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<ValidaIdUnico>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value == null) {
            return true
        }
        return try {
            UUID.fromString(value)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}