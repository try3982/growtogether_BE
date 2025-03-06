package com.campfiredev.growtogether.study.schedule;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalTimeConverter implements AttributeConverter<LocalTime, String> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

  @Override
  public String convertToDatabaseColumn(LocalTime time) {
    return (time == null) ? null : time.format(FORMATTER);
  }

  @Override
  public LocalTime convertToEntityAttribute(String dbData) {
    return (dbData == null) ? null : LocalTime.parse(dbData, FORMATTER);
  }
}
