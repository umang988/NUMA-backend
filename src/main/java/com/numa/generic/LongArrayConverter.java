package com.numa.generic;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter
public class LongArrayConverter implements AttributeConverter<Long[], String> {

    @Override
    public String convertToDatabaseColumn(Long[] attribute) {
        if (attribute == null || attribute.length == 0) {
            return null;
        }
        // Convert Long[] to a comma-separated String
        return Arrays.stream(attribute)
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse(null);
    }

    @Override
    public Long[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new Long[0];
        }
        // Convert comma-separated String to Long[]
        return Arrays.stream(dbData.split(","))
                .map(Long::valueOf)
                .toArray(Long[]::new);
    }
}
