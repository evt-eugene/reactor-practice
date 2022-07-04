package com.example.demo.student.entity;

import java.util.UUID;

public final class Librarian {

  private final UUID id;
  private final FullName fullName;
  private final byte age;
  private final long version;

  public Librarian(UUID id, FullName name, byte age, long version) {
    this.id = id;
    this.fullName = name;
    this.age = age;
    this.version = version;
  }

  public UUID getId() {
    return id;
  }

  public FullName getFullName() {
    return fullName;
  }

  public byte getAge() {
    return age;
  }

  public long getVersion() {
    return version;
  }
}
