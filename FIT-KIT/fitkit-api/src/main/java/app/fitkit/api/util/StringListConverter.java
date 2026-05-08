package app.fitkit.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    // Jackson's ObjectMapper is the engine Spring uses to parse JSON
    private static final ObjectMapper mapper = new ObjectMapper();

    // 1. Converts Java List -> JSON String (When saving to Neon DB)
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            if (attribute == null || attribute.isEmpty()) {
                return "[]";
            }
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting list to JSON string", e);
        }
    }

    // 2. Converts JSON String -> Java List (When reading from Neon DB)
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.trim().isEmpty()) {
                return new ArrayList<>();
            }
            // TypeReference tells Jackson specifically to build a List of Strings
            return mapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON string to list", e);
        }
    }
}