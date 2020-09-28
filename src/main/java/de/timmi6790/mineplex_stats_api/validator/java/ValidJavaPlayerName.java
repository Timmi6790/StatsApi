package de.timmi6790.mineplex_stats_api.validator.java;

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
