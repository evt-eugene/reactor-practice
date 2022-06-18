package com.example.demo.student.web;

import java.beans.ConstructorProperties;

public class BookPublishingYearDto {

  private final int publishingYear;

  @ConstructorProperties("publishingYear")
  public BookPublishingYearDto(int publishingYear) {
    this.publishingYear = publishingYear;
  }

  public int getPublishingYear() {
    return publishingYear;
  }
}
