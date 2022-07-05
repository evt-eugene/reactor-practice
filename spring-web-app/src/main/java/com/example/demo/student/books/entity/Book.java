package com.example.demo.student.books.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table("books")
public class Book {

  @PrimaryKey("id")
  private UUID id;

  @Column("title")
  private String title;

  @Column("publishing_year")
  private int publishingYear;

  protected Book() {
  }

  public Book(UUID id, String title, int publishingYear) {
    this.id = id;
    this.title = title;
    this.publishingYear = publishingYear;
  }

  public UUID getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getPublishingYear() {
    return publishingYear;
  }

  public void setPublishingYear(int publishingYear) {
    this.publishingYear = publishingYear;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Book book = (Book) o;
    return id.equals(book.id) && title.equals(book.title) && publishingYear == book.publishingYear;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, publishingYear);
  }
}

