package com.example.demo.student.janitors.entity;

import java.util.Collections;
import java.util.List;

public final class Responsibility {

  private final String description;
  private final List<Skill> skills;

  public Responsibility(String description, List<Skill> skills) {
    this.description = description;
    this.skills = skills;
  }

  public String getDescription() {
    return description;
  }

  public List<Skill> getSkills() {
    return Collections.unmodifiableList(skills);
  }
}
