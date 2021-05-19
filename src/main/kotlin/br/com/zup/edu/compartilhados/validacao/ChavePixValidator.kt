package br.com.zup.edu.compartilhados.validacao

import br.com.zup.edu.TipoDeChave
import br.com.zup.edu.cadastro.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton

@Singleton
class ChavePixValidator : ConstraintValidator<ValidaChavePix, NovaChavePix> {

    override fun isValid(
        novaChave: NovaChavePix,
        annotationMetadata: AnnotationValue<ValidaChavePix>,
        context: ConstraintValidatorContext,
    ): Boolean {

        if (novaChave.tipoDeChave == null) {
            return false
        }

        val valorChave = when (novaChave.tipoDeChave) {
            TipoDeChave.ALEATORIA -> br.com.zup.edu.cadastro.TipoDeChave.ALEATORIA
            TipoDeChave.CELULAR -> br.com.zup.edu.cadastro.TipoDeChave.CELULAR
            TipoDeChave.CPF -> br.com.zup.edu.cadastro.TipoDeChave.CPF
            TipoDeChave.EMAIL -> br.com.zup.edu.cadastro.TipoDeChave.EMAIL

            else -> return false
        }
        return valorChave.valida(novaChave.valor, context)
    }

}
