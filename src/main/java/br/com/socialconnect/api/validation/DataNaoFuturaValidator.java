package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DataNaoFuturaValidator implements ConstraintValidator<DataNaoFutura, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.isAfter(LocalDate.now());
    }
}
