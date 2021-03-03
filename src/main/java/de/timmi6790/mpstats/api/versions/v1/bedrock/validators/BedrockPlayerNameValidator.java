package de.timmi6790.mpstats.api.versions.v1.bedrock.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class BedrockPlayerNameValidator implements ConstraintValidator<ValidBedrockPlayerName, String> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^.{3,32}$");

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return NAME_PATTERN.matcher(value).find();
    }
}
