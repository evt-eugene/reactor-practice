package com.example.demo.student.librarians.web;

public final class LibrarianDto {

  private final String firstName;
  private final String lastName;
  private final String middleName;

  private byte age;

  public LibrarianDto(String firstName, String lastName, String middleName, byte age) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.middleName = middleName;
    this.age = age;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public byte getAge() {
    return age;
  }
}
