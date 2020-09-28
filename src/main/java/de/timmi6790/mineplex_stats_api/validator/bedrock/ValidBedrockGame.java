package de.timmi6790.mineplex_stats_api.validator.bedrock;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BedrockGameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBedrockGame {
    String message() default "Invalid bedrock game";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
