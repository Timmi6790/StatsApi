package de.timmi6790.mpstats.api.exceptions.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class CustomErrorController extends AbstractErrorController {
    public CustomErrorController(final ErrorAttributes errorAttributes) {
        super(errorAttributes, Collections.emptyList());
    }

    @RequestMapping
    @Operation(hidden = true)
    public ResponseEntity<Map<String, Object>> error(final HttpServletRequest request) {
        final Map<String, Object> body = this.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        final HttpStatus status = this.getStatus(request);
        return new ResponseEntity<>(body, status);
    }

}
