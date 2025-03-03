package me.owlaukka.currencyconversion;

import java.util.List;

public class CustomValidationException extends RuntimeException {
    private final List<String> fields;
    private final String message;

    public CustomValidationException(String message, List<String> fields) {
        this.fields = fields;
        this.message = message;
    }

    public List<String> getFields() {
        return fields;
    }

    public String getMessage() {
        return message;
    }
}
