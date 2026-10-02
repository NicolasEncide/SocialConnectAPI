package br.com.socialconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DataNaoFuturaValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DataNaoFutura {
    String message() default "{DataNaoFutura.dataDoacao}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
