package com.ciblorgasport.eventservice.dto.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class FlexibleLongDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_NUMBER_INT) {
            return parser.getLongValue();
        }

        if (token == JsonToken.VALUE_STRING) {
            String value = parser.getText();
            if (value == null) {
                return null;
            }
            String trimmed = value.trim();
            if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) {
                return null;
            }
            try {
                return Long.valueOf(trimmed);
            } catch (NumberFormatException ex) {
                throw InvalidFormatException.from(parser, "Invalid Long value", trimmed, Long.class);
            }
        }

        if (token == JsonToken.START_OBJECT) {
            JsonNode node = parser.readValueAsTree();
            JsonNode idNode = node.get("id");
            if (idNode == null || idNode.isNull()) {
                return null;
            }
            if (idNode.isIntegralNumber()) {
                return idNode.longValue();
            }
            if (idNode.isTextual()) {
                String text = idNode.asText();
                try {
                    return Long.valueOf(text.trim());
                } catch (NumberFormatException ex) {
                    throw InvalidFormatException.from(parser, "Invalid id value in object", text, Long.class);
                }
            }
            throw InvalidFormatException.from(parser, "Unsupported id value type in object", idNode.toString(), Long.class);
        }

        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        throw InvalidFormatException.from(parser, "Expected a Long value or object with id", parser.getValueAsString(), Long.class);
    }
}