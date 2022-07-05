package com.example.demo.student.janitors.service.impl;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.demo.student.janitors.entity.Janitor;
import com.example.demo.student.janitors.entity.Responsibility;
import com.example.demo.student.janitors.service.JanitorService;
import com.example.demo.student.janitors.web.JanitorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
public class ReactiveCassandraTemplateJanitorService implements JanitorService {

  private final ReactiveCassandraTemplate template;

  @Autowired
  public ReactiveCassandraTemplateJanitorService(ReactiveCassandraTemplate template) {
    this.template = template;
  }

  @Override
  public Flux<Janitor> findAll() {
    return template.query(Janitor.class).all();
  }

  @Override
  public Mono<Janitor> findById(UUID id) {
    return template.selectOneById(id, Janitor.class);
  }

  @Override
  public Mono<Janitor> createJanitor(JanitorDto dto) {
    return Mono.defer(() -> {
          var responsibility = new Responsibility(dto.getDesc(), dto.getSkills());
          var janitor = new Janitor(Uuids.timeBased(), dto.getName(), responsibility);

          return Mono.just(janitor);
        })
        .flatMap(janitor -> {
          var options = InsertOptions.builder().consistencyLevel(ConsistencyLevel.ONE).build();
          return template.insert(Janitor.class).withOptions(options).one(janitor);
        })
        .map(EntityWriteResult::getEntity);
  }

  @Override
  public Mono<Janitor> updateJanitor(UUID id, JanitorDto dto) {
    return findById(id)
        .flatMap(janitor -> {
          janitor.setName(dto.getName());
          janitor.setResponsibility(new Responsibility(dto.getDesc(), dto.getSkills()));

          var options = UpdateOptions.builder().consistencyLevel(ConsistencyLevel.ONE).build();
          return template.update(janitor, options);
        })
        .map(EntityWriteResult::getEntity);
  }

  @Override
  public Mono<Boolean> deleteJanitor(UUID id) {
    return findById(id)
        .flatMap(janitor -> {
          var options = DeleteOptions.builder().consistencyLevel(ConsistencyLevel.ONE).build();
          return template.delete(janitor, options);
        })
        .map(WriteResult::wasApplied);
  }
}
