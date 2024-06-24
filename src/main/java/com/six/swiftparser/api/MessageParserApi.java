package com.six.swiftparser.api;

import com.six.swiftparser.service.MessageParser;
import com.six.swiftparser.domain.MessageParsingError;
import com.six.swiftparser.domain.MessageParsingResult;
import com.six.swiftparser.domain.exception.InvalidField;
import com.six.swiftparser.domain.exception.InvalidMessage;

import java.util.Objects;

public final class MessageParserApi {
    private final MessageParser messageParser;

    public MessageParserApi(MessageParser messageParser) {
        this.messageParser = messageParser;
    }

    public MessageParsingResult parseSwiftMessage(String message) {
        try {
            if (Objects.isNull(message)) {
                throw new InvalidMessage("Empty message");
            }
            final var fixedLengthMessage = messageParser.parse(message);
            return new MessageParsingResult(fixedLengthMessage, null);
        } catch (InvalidField e) {
            return new MessageParsingResult(null, new MessageParsingError("Unable to parse swift message", e.getFieldErrors()));
        } catch (InvalidMessage e) {
            return new MessageParsingResult(null, new MessageParsingError("Invalid swift message", null));
        }
    }

}

