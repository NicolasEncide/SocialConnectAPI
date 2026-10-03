package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EstoqueNaoNegativoValidator implements ConstraintValidator<EstoqueNaoNegativo, Integer> {
    @Override
    public boolean isValid(Integer numero, ConstraintValidatorContext context) {
        if (numero == null) {
            return true;
        }
        return numero >= 0;
    }
}
