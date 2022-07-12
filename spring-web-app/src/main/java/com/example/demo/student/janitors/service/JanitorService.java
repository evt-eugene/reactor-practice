package com.example.demo.student.janitors.service;

import com.example.demo.student.janitors.entity.Janitor;
import com.example.demo.student.janitors.entity.projections.JanitorChemicalView;
import com.example.demo.student.janitors.entity.projections.TotalJanitorChemicalView;
import com.example.demo.student.janitors.web.JanitorDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JanitorService {

  Flux<Janitor> findAll();

  Flux<JanitorChemicalView> findAllChemicalViews();

  Mono<TotalJanitorChemicalView> getTotalChemicalView();

  Mono<Janitor> findById(UUID id);

  Flux<Janitor> findByCharacteristic(String characteristic);

  Mono<Janitor> createJanitor(JanitorDto dto);

  Mono<Janitor> updateJanitor(UUID id, JanitorDto dto);

  Mono<Boolean> deleteJanitor(UUID id);

}
