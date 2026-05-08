package app.fitkit.api.util;

import app.fitkit.api.dto.ParsedItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class ParsedItemListConverter implements AttributeConverter<List<ParsedItem>, String> {

    // Jackson's JSON parser engine
    private static final ObjectMapper mapper = new ObjectMapper();

    // 1. Converts Java List<ParsedItem> -> JSON String (When saving to Neon DB)
    @Override
    public String convertToDatabaseColumn(List<ParsedItem> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return "[]";
            }
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // If Jackson fails to parse it, we throw a crash error rather than corrupting the database
            throw new IllegalArgumentException("Error converting ParsedItem list to JSON string", e);
        }
    }

    // 2. Converts JSON String -> Java List<ParsedItem> (When reading from Neon DB)
    @Override
    public List<ParsedItem> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.trim().isEmpty()) {
                return new ArrayList<>();
            }
            // TypeReference specifically instructs Jackson to rebuild the exact ParsedItem objects
            return mapper.readValue(dbData, new TypeReference<List<ParsedItem>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON string to ParsedItem list", e);
        }
    }
}