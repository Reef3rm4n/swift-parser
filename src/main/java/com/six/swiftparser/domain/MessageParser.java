package com.six.swiftparser.domain;

import com.six.swiftparser.domain.exception.InvalidField;
import com.six.swiftparser.domain.exception.InvalidMessage;

public sealed interface MessageParser permits SwiftToJsonParser {

    String parse(String message) throws InvalidField, InvalidMessage;

    String getTargetFormat();

    String getSourceFormat();
}
