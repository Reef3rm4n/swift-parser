package com.six.swiftparser.domain;

import java.util.Map;

public record MessageParsingError(
        String message,
        Map<String, String> details
) {
}
