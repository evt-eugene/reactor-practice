package com.example.demo.student.entity;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Year;
import java.util.UUID;

@Table("books")
public class Book {

  @PrimaryKey
  private final UUID id;
  private String title;

  @Column("publishing_year")
  private Year publishingYear;

  public Book(UUID id, String title, Year publishingYear) {
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

  public Year getPublishingYear() {
    return publishingYear;
  }

  public void setPublishingYear(Year publishingYear) {
    this.publishingYear = publishingYear;
  }
}

