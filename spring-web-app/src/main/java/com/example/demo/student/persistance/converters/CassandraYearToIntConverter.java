package com.example.demo.student.persistance.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.Year;

@WritingConverter
public enum CassandraYearToIntConverter implements Converter<Year, Integer> {

  INSTANCE;

  @Override
  public Integer convert(Year year) {
    return year.getValue();
  }
}