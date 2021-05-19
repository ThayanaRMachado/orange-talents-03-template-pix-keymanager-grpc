package br.com.zup.edu.cadastro

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import io.micronaut.validation.validator.constraints.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoDeChave {

    CPF {
        override fun valida(valor: String?, context: ConstraintValidatorContext): Boolean {
            if (valor == null) {
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(valor, null)
            }
        }
    },

    CELULAR {
        val regex = Regex("^\\+[1-9][0-9]\\d{1,14}\$")
        override fun valida(valor: String?, context: ConstraintValidatorContext): Boolean {
            if (valor.isNullOrBlank()) {
                return false
            }
            return valor.matches(regex)
        }
    },

    EMAIL {
        override fun valida(valor: String?, context: ConstraintValidatorContext): Boolean {
            if (valor == null) {
                return false
            }
            return EmailValidator().isValid(valor, AnnotationValue("email"), context)
        }
    },

    ALEATORIA {
        override fun valida(chave: String?, context: ConstraintValidatorContext): Boolean = true
    };

    abstract fun valida(chave: String?, context: ConstraintValidatorContext): Boolean
}