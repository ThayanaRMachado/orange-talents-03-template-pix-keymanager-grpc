package br.com.zup.edu.cadastro.compartilhado.validacao

import br.com.zup.edu.TipoChave
import br.com.zup.edu.cadastro.NovaChave
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.EmailValidator
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Target(CLASS, TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = [ChavePixValidator::class])
annotation class ValidaChavePix(
    val message: String = "Chave Pix inválida!",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ChavePixValidator : ConstraintValidator<ValidaChavePix, NovaChave> {

    fun isValid(
        novaChave: NovaChave?,
        annotationMetadata: AnnotationValue<ValidaChavePix>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (novaChave == null || (novaChave.valor == null || novaChave.tipo == null)) {
            return false
        }
        val valor = novaChave.valor
        val tipo = novaChave.tipo

        if (tipo == TipoChave.CPF){
            return validaCpf(valor)
        }

        if (tipo == TipoChave.EMAIL){
            return validaEmail(valor)
        }

        if (tipo == TipoChave.CELULAR){
            return validaCelular(valor)
        }
        return false
    }

    private fun validaCpf(valor: String): Boolean {
        return valor.matches("^[0-9]{11}\$".toRegex())
    }

    private fun validaEmail(valor: String): Boolean {
        return EmailValidator().run {
            initialize(null)
            isValid(valor, null)
        }
    }

    private fun validaCelular(valor: String): Boolean {
        return valor.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
    }

    override fun isValid(value: NovaChave?, context: ConstraintValidatorContext?): Boolean {
        TODO("Not yet implemented")
    }

}