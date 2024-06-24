package com.six.swiftparser.infrastructure;

import com.six.swiftparser.api.MessageParserApi;
import com.six.swiftparser.domain.MessageParsingError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class MessageParserHttpRoute {
    private final MessageParserApi api;

    public MessageParserHttpRoute(@Autowired MessageParserApi api) {
        this.api = api;
    }

    @PostMapping(value = "/parse-swift-message", consumes = "application/xml", produces = "application/json")
    @Operation(summary = "Parse and convert SWIFT XML message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful response",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(
                                    implementation = SwiftTransactionResponse.class,
                                    example = "{\"transactionId\":\"00012345\",\"transactionAmount\":\"0001000.00\",\"transactionCurrency\":\"USD\",\"transactionDate\":\"2024-05-28\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MessageParsingError.class,
                                    example = "{\"error\":\"SWIFT message parsing error\",\"details\":[\"Missing or empty required field: /swift/transaction/id\"]}"
                            )
                    )
            )
    })
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_XML_VALUE,
                    schema = @Schema(
                            implementation = Swift.class,
                            example = "<swift><transaction><id>12345</id><amount>1000.00</amount><currency>USD</currency><date>2024-05-28</date></transaction></swift>"
                    )
            )
    )
    public ResponseEntity<Object> parseSwiftMessage(HttpServletRequest swiftMessage) throws IOException {
        final var result = api.parseSwiftMessage(new String(swiftMessage.getInputStream().readAllBytes()));
        if (result.failed()) {
            return new ResponseEntity<>(result.error(), HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(result.response());
        }
    }

    @Schema(description = "Swift message containing transaction details.")
    @XmlRootElement(name = "swift")
    public record Swift(
            @Schema(description = "Transaction details")
            SwiftTransaction transaction
    ) {
    }

    @Schema(description = "Details of a SWIFT transaction.")
    public record SwiftTransaction(
            @Schema(description = "Unique identifier of the transaction", example = "12345")
            int id,

            @Schema(description = "Transaction amount in the specified currency", example = "1000.00")
            double amount,

            @Schema(description = "Currency code as per ISO 4217", example = "USD")
            String currency,

            @Schema(description = "Date of the transaction in YYYY-MM-DD format", example = "2024-05-28")
            String date
    ) {
    }

    @Schema(description = "Details of a SWIFT transaction.")
    public record SwiftTransactionResponse(
            @Schema(description = "Unique identifier of the transaction", example = "12345")
            int transactionId,

            @Schema(description = "Transaction amount in the specified currency", example = "1000.00")
            double transactionAmount,

            @Schema(description = "Currency code as per ISO 4217", example = "USD")
            String transactionCurrency,

            @Schema(description = "Date of the transaction in YYYY-MM-DD format", example = "2024-05-28")
            String transactionDate
    ) {
    }

}

