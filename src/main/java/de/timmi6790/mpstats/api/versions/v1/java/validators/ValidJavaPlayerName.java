package de.timmi6790.mpstats.api.versions.v1.java.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = JavaPlayerNameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJavaPlayerName {
    String message() default "Invalid java player name";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
