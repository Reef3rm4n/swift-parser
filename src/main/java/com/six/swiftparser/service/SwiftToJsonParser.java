package com.six.swiftparser.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.six.swiftparser.domain.FieldProperty;
import com.six.swiftparser.domain.SwiftFormatProperties;
import com.six.swiftparser.domain.exception.InvalidField;
import com.six.swiftparser.domain.exception.InvalidMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.*;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathConstants;

public final class SwiftToJsonParser implements MessageParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SwiftToJsonParser.class);
    private final SwiftFormatProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SwiftToJsonParser(SwiftFormatProperties properties) {
        this.properties = properties;
    }


    public String parse(String swiftMessage) throws InvalidField, InvalidMessage {
        final Map<String, String> errors = new HashMap<>(4);
        final Map<String, String> result = new HashMap<>(4);
        final var document = parseSwiftMessage(swiftMessage);
        final var xpath = XPathFactory.newInstance().newXPath();
        for (FieldProperty prop : properties.properties()) {
            getValue(document, xpath, errors, prop)
                    .ifPresent(value -> result.put(prop.name(), value));
        }
        if (!errors.isEmpty()) {
            throw new InvalidField(errors);
        }
        if (!result.isEmpty()) {
            try {
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                throw new InvalidMessage(e);
            }
        } else {
            throw new InvalidMessage("Unable to extract required message fields");
        }
    }


    private static Document parseSwiftMessage(String swiftMessage) throws InvalidMessage {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(swiftMessage)));
        } catch (Exception e) {
            LOGGER.error("Error parsing swift message {}", swiftMessage, e);
            throw new InvalidMessage(e);
        }
    }

    private Optional<String> getValue(Document document, XPath xpath, Map<String, String> errors, FieldProperty property) {
        try {
            XPathExpression xPathExpression = xpath.compile(property.pathExpression());
            String value = (String) xPathExpression.evaluate(document, XPathConstants.STRING);
            if (value == null || value.isEmpty()) {
                if (property.required()) {
                    errors.put(property.name(), "value not present or empty");
                }
                return Optional.empty();
            } else {
                return Optional.of(switch (property.type()) {
                    case INT -> String.format(property.format(), Integer.parseInt(value));
                    case DOUBLE -> String.format(property.format(), Double.parseDouble(value));
                    case STRING -> String.format(property.format(), value);
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error parsing message field {}", property, e);
            errors.put(property.name(), "Error parsing message field");
            return Optional.empty();
        }
    }
}

