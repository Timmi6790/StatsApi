package de.timmi6790.mpstats.api.versions.v1.bedrock.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BedrockGameValidator implements ConstraintValidator<ValidBedrockGame, String> {
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return true;
    }
}
