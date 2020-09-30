package de.timmi6790.mineplex_stats_api.validator.java;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class JavaPlayerNameValidator implements ConstraintValidator<ValidJavaPlayerName, String> {
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\w{1,16}$");

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return NAME_PATTERN.matcher(value).find();
    }
}
