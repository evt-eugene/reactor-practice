package com.example.demo.student.service;

import com.example.demo.student.entity.janitor.Janitor;
import com.example.demo.student.web.JanitorDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JanitorService {

  Flux<Janitor> findAll();

  Mono<Janitor> findById(UUID id);

  Mono<Janitor> createJanitor(JanitorDto dto);

  Mono<Janitor> updateJanitor(UUID uuid, JanitorDto dto);

}
