package com.example.demo.student.janitors.web;

import com.example.demo.student.janitors.entity.Skill;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public final class JanitorDto {

  @JsonProperty("name")
  private final String name;

  @JsonProperty("responsibility")
  private final ResponsibilityDto responsibilityDto;

  @JsonProperty("characteristic")
  private final String characteristic;

  public JanitorDto(String name, ResponsibilityDto responsibilityDto, String characteristic) {
    this.name = name;
    this.responsibilityDto = responsibilityDto;
    this.characteristic = characteristic;
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

  public String getCharacteristic() {
    return characteristic;
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
