package com.six.swiftparser.domain.exception;

import java.util.Map;

public class InvalidField extends Exception {

    private final Map<String, String> fieldErrors;

    public InvalidField(Map<String, String> fieldErrors) {
        super("Invalid swift message fields");
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}
