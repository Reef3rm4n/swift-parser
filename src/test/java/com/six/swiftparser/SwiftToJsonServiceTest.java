package com.example.swiftparser;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.six.swiftparser.infrastructure.Main;
import com.six.swiftparser.domain.SwiftToJsonParser;
import com.six.swiftparser.domain.exception.InvalidField;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = Main.class)
class SwiftToJsonServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SwiftToJsonParser service;

    private static Stream<Arguments> provideValidSwiftMessages() {
        return Stream.of(
                Arguments.of("<swift><transaction><id>12345</id><amount>1000.00</amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>",
                        "{\"transactionId\":\"00012345\",\"transactionAmount\":\"0001000.00\",\"transactionCurrency\":\"USD\",\"transactionDate\":\"2024-05-28\"}"),
                Arguments.of("<swift><transaction><id>1</id><amount>20.5</amount><currency>EUR</currency><date>2023-01-01</date></transaction></swift>",
                        "{\"transactionId\":\"00000001\",\"transactionAmount\":\"0000020.50\",\"transactionCurrency\":\"EUR\",\"transactionDate\":\"2023-01-01\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidSwiftMessages")
    void testParseAndConvert(String xmlMessage, String expectedJson) {
        try {
            String result = service.parse(xmlMessage);
            Map<String, Object> resultMap = objectMapper.readValue(result, new TypeReference<>() {});
            Map<String, Object> expectedMap = objectMapper.readValue(expectedJson, new TypeReference<>() {});

            assertEquals(expectedMap, resultMap);
        } catch (Exception e) {
            fail("Parsing failed with exception: " + e.getMessage());
        }
    }

    private static Stream<Arguments> provideInvalidSwiftMessages() {
        return Stream.of(
                Arguments.of("<swift><transaction><id></id><amount>1000.00</amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>", "transactionId"),
                Arguments.of("<swift><transaction><id>12345</id><amount></amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>", "transactionAmount"),
                Arguments.of("<swift><transaction><id>12345</id><amount>1000.00</amount><currency></currency><date>2024-05-28</date></transaction></swift>", "transactionCurrency"),
                Arguments.of("<swift><transaction><id>12345</id><amount>1000.00</amount><currency>USD</currency></transaction></swift>", "transactionDate")
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidSwiftMessages")
    void testParseAndConvertInvalidXML(String xmlMessage, String expectedMissingField) {
        try {
            service.parse(xmlMessage);
            fail("Expected InvalidSwiftMessage to be thrown");
        } catch (InvalidField e) {
            assertTrue(e.getFieldErrors().containsKey(expectedMissingField));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
