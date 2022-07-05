package com.example.demo.student.greeting.entity;

import java.beans.ConstructorProperties;

public class Greeting {

  private final String message;

  @ConstructorProperties("message")
  public Greeting(String message) {
    this.message = message;
  }

  public String getMessage() {
    return this.message;
  }

  @Override
  public String toString() {
    return "Greeting{" + "message='" + message + '\'' + '}';
  }
}