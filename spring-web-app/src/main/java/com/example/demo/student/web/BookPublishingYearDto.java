package com.example.demo.student.web;

import java.beans.ConstructorProperties;
import java.time.Year;

public class BookPublishingYearDto {

  private final int publishingYear;

  @ConstructorProperties("publishingYear")
  public BookPublishingYearDto(int publishingYear) {
    this.publishingYear = publishingYear;
  }

  public Year getPublishingYear() {
    return Year.of(publishingYear);
  }
}
