package com.example.demo.student.janitors.service.impl;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.demo.student.janitors.entity.Janitor;
import com.example.demo.student.janitors.entity.Responsibility;
import com.example.demo.student.janitors.persistance.JanitorRepository;
import com.example.demo.student.janitors.service.JanitorService;
import com.example.demo.student.janitors.web.JanitorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ReactiveCassandraTemplateJanitorService implements JanitorService {

  private final JanitorRepository repository;

  @Autowired
  public ReactiveCassandraTemplateJanitorService(JanitorRepository repository) {
    this.repository = repository;
  }

  @Override
  public Flux<Janitor> findAll() {
    return repository.findAll();
  }

  @Override
  public Mono<Janitor> findById(UUID id) {
    return repository.findById(id);
  }

  @Override
  public Flux<Janitor> findByCharacteristic(String characteristic) {
    return repository.findByCharacteristic(characteristic);
  }

  @Override
  public Mono<Janitor> createJanitor(JanitorDto dto) {
    return Mono.defer(() -> {
      var responsibility = new Responsibility(dto.getDesc(), dto.getSkills());
      var janitor = new Janitor(Uuids.timeBased(), dto.getName(), responsibility, dto.getCharacteristic());

      return repository.save(janitor);
    });
  }

  @Override
  public Mono<Janitor> updateJanitor(UUID id, JanitorDto dto) {
    return findById(id)
        .flatMap(janitor -> {
          janitor.setName(dto.getName());
          janitor.setResponsibility(new Responsibility(dto.getDesc(), dto.getSkills()));
          janitor.setCharacteristic(dto.getCharacteristic());

          return repository.update(janitor);
        });
  }

  @Override
  public Mono<Boolean> deleteJanitor(UUID id) {
    return findById(id)
        .flatMap(janitor -> repository.delete(janitor));
  }
}
