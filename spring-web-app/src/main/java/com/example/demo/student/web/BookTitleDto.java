package com.example.demo.student.web;

import java.beans.ConstructorProperties;

public final class BookTitleDto {

  private final String title;

  @ConstructorProperties("title")
  public BookTitleDto(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }
}
