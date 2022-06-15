package com.example.demo.student.web;

import java.beans.ConstructorProperties;

public final class BookDto {

  private final String title;

  @ConstructorProperties("title")
  public BookDto(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }
}
