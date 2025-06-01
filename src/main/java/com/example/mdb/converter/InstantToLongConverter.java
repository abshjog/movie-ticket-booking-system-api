package com.example.mdb.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;

@Converter(autoApply = true)
public class InstantToLongConverter implements AttributeConverter<Instant, Long> {

    @Override
    public Long convertToDatabaseColumn(Instant instant) {
        return (instant == null) ? null : instant.toEpochMilli();
    }

    @Override
    public Instant convertToEntityAttribute(Long dbData) {
        return (dbData == null) ? null : Instant.ofEpochMilli(dbData);
    }
}
