package com.efundzz.dmnservice.Exception;

import java.util.Map;


@SuppressWarnings("serial")
public class ValidationException extends RuntimeException {


    private final Map<String, String> fieldErrors;

    public ValidationException(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
