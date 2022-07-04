package com.example.demo.student.web;

import com.example.demo.student.entity.janitor.Skill;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public final class JanitorDto {

  @JsonProperty("name")
  private final String name;

  @JsonProperty("responsibility")
  private final ResponsibilityDto responsibilityDto;

  public JanitorDto(String name, ResponsibilityDto responsibilityDto) {
    this.name = name;
    this.responsibilityDto = responsibilityDto;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return responsibilityDto.getDesc();
  }

  public List<Skill> getSkills() {
    return responsibilityDto.getSkills();
  }

  public static final class ResponsibilityDto {

    @JsonProperty("desc")
    private final String desc;

    @JsonProperty("skills")
    private final List<Skill> skills;

    public ResponsibilityDto(String desc, List<Skill> skills) {
      this.desc = desc;
      this.skills = Collections.unmodifiableList(skills);
    }

    public String getDesc() {
      return desc;
    }

    public List<Skill> getSkills() {
      return skills;
    }
  }
}
