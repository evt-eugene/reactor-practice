package com.example.demo.student.janitors.entity;

import org.springframework.data.annotation.Version;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Embedded;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("janitors")
public class Janitor {

  @PrimaryKey("id")
  private UUID id;

  @Column("name")
  private String name;

  @Embedded.Nullable
  private Responsibility responsibility;

  @Column("characteristic")
  private String characteristic;

  @Version
  @Column("version")
  private long version;

  protected Janitor() {
    // JPA
  }

  public Janitor(UUID id, String name, Responsibility responsibility, String characteristic) {
    this.id = id;
    this.name = name;
    this.responsibility = responsibility;
    this.characteristic = characteristic;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Responsibility getResponsibility() {
    return responsibility;
  }

  public void setResponsibility(Responsibility responsibility) {
    this.responsibility = responsibility;
  }

  public String getCharacteristic() {
    return characteristic;
  }

  public void setCharacteristic(String characteristic) {
    this.characteristic = characteristic;
  }
}
