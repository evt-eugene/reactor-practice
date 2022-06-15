package com.example.demo.student.entity;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("books")
public class Book {

  @PrimaryKey
  private final UUID id;
  private String title;

  public Book(UUID id, String title) {
    this.id = id;
    this.title = title;
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
}

