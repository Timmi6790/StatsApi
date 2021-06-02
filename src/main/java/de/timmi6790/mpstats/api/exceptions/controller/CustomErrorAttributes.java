package de.timmi6790.mpstats.api.exceptions.controller;

import de.timmi6790.mpstats.api.exceptions.RestException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(final WebRequest webRequest, final ErrorAttributeOptions options) {
        final Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);

        final Throwable exception = this.getError(webRequest);
        if (exception instanceof final RestException restException) {
            for (final Map.Entry<String, Object> entry : restException.getErrorAttributes().entrySet()) {
                errorAttributes.put(entry.getKey(), entry.getValue());
            }
        }

        return errorAttributes;
    }
}