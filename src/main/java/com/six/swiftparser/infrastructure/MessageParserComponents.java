package com.six.swiftparser.infrastructure;

import com.six.swiftparser.api.MessageParserApi;
import com.six.swiftparser.service.MessageParser;
import com.six.swiftparser.service.SwiftToJsonParser;
import com.six.swiftparser.domain.FieldProperty;
import com.six.swiftparser.domain.FieldType;
import com.six.swiftparser.domain.SwiftFormatProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Set;
@Component
public class MessageParserComponents {

    @Bean
    public SwiftFormatProperties swiftFormatProperties() {
        return new SwiftFormatProperties(
                Set.of(
                        new FieldProperty("transactionId", "/swift/transaction/id", "%08d", true, FieldType.INT),
                        new FieldProperty("transactionAmount", "/swift/transaction/amount", "%010.2f", true, FieldType.DOUBLE),
                        new FieldProperty("transactionCurrency", "/swift/transaction/currency", "%-3s", true, FieldType.STRING),
                        new FieldProperty("transactionDate", "/swift/transaction/date", "%s", true, FieldType.STRING)
                )
        );
    }

    @Bean
    public MessageParser messageParser(SwiftFormatProperties properties) {
        return new SwiftToJsonParser(properties);
    }

    @Bean
    public MessageParserApi api(MessageParser messageParser) {
        return new MessageParserApi(messageParser);
    }

}
