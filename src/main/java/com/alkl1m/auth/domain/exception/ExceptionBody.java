package com.alkl1m.auth.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Тело исключения для передачи клиенту.
 *
 * @author alkl1m
 */
@Getter
@Setter
@AllArgsConstructor
public class ExceptionBody {

    private String message;
    private Map<String, String> errors;

    public ExceptionBody(
            final String message
    ) {
        this.message = message;
    }

}
