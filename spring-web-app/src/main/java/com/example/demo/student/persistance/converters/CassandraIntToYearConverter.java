package com.example.demo.student.persistance.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Year;

@ReadingConverter
public enum CassandraIntToYearConverter implements Converter<Integer, Year> {

  INSTANCE;

  @Override
  public Year convert(Integer value) {
    return value == null ? null : Year.of(value);
  }
}