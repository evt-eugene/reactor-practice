package com.example.demo.student.janitors.entity.projections;

import com.example.demo.student.janitors.entity.Skill;

import java.util.List;
import java.util.UUID;

public record JanitorChemicalView(UUID id, String name, List<Skill> skills, Integer worksWithChemicals) {
}
