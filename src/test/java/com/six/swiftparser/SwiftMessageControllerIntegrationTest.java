package com.six.swiftparser;

import com.six.swiftparser.infrastructure.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
class SwiftMessageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
    }

    @Test
    void testParseAndConvert_ValidMessage() throws Exception {
        String validXmlMessage = "<swift><transaction><id>12345</id><amount>1000.00</amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>";
        String expectedJson = "{\"transactionId\":\"00012345\",\"transactionAmount\":\"0001000.00\",\"transactionCurrency\":\"USD\",\"transactionDate\":\"2024-05-28\"}";
        mockMvc.perform(post("/api/parse-swift-message")
                        .content(validXmlMessage)
                        .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    void testParseAndConvert_InvalidMessage_MissingField() throws Exception {
        String invalidXmlMessage = "<swift><transaction><id></id><amount>1000.00</amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>";

        mockMvc.perform(post("/api/parse-swift-message")
                        .content(invalidXmlMessage)
                        .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Unable to parse swift message\",\"details\":{\"transactionId\":\"value not present or empty\"}}"));
    }

    @Test
    void testParseAndConvert_InvalidMessage_GeneralError() throws Exception {
        String invalidXmlMessage = "<swift><transaction><id>invalid</id><amount>1000.00</amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>";

        mockMvc.perform(post("/api/parse-swift-message")
                        .content(invalidXmlMessage)
                        .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Unable to parse swift message\",\"details\":{\"transactionId\":\"Error parsing message field\"}}"));
    }

    @Test
    void testParseAndConvert_InvalidXmlFormat() throws Exception {
        String invalidXmlFormatMessage = "<swift><transaction><id>12345<amount>1000.00</amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>";

        mockMvc.perform(post("/api/parse-swift-message")
                        .content(invalidXmlFormatMessage)
                        .contentType(MediaType.APPLICATION_XML))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Invalid swift message\",\"details\":\"org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 103; The element type \\\"id\\\" must be terminated by the matching end-tag \\\"</id>\\\".\"}"));
    }
}

