package com.six.swiftparser.domain;


public record FieldProperty(
        String name,
        String pathExpression,
        String format,
        boolean required,
        FieldType type
) {

}
