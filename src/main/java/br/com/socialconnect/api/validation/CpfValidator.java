package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) {
            return true;
        }

        String digits = cpf.replaceAll("\\D", "");

        if (digits.length() != 11) {
            return false;
        }

        if (digits.chars().distinct().count() == 1) {
            return false;
        }

        int sum1 = 0;
        for (int i = 0; i < 9; i++) {
            sum1 += (digits.charAt(i) - '0') * (10 - i);
        }
        int remainder1 = 11 - (sum1 % 11);
        int digit1 = (remainder1 >= 10) ? 0 : remainder1;

        if (digit1 != (digits.charAt(9) - '0')) {
            return false;
        }

        int sum2 = 0;
        for (int i = 0; i < 10; i++) {
            sum2 += (digits.charAt(i) - '0') * (11 - i);
        }
        int remainder2 = 11 - (sum2 % 11);
        int digit2 = (remainder2 >= 10) ? 0 : remainder2;

        return digit2 == (digits.charAt(10) - '0');
    }
}
