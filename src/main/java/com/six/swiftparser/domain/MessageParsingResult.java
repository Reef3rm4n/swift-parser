package com.six.swiftparser.domain;

import java.util.Objects;

public record MessageParsingResult(
        String response,
        MessageParsingError error
) {

    public boolean failed() {
        return Objects.nonNull(error);
    }
}
