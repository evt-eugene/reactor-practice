package com.example.demo.student.janitors.persistance.impl;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.example.demo.student.janitors.entity.Janitor;
import com.example.demo.student.janitors.entity.projections.JanitorChemicalView;
import com.example.demo.student.janitors.persistance.JanitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.*;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.cassandra.core.query.Criteria.where;
import static org.springframework.data.cassandra.core.query.Query.query;

@Repository
public class ReactiveCassandraTemplateJanitorRepository implements JanitorRepository {

  private static final String SELECT_JANITOR_CHEMICAL_VIEWS = """
        SELECT id, name, skills, count_janitor_if_works_with_chemicals(skills) as worksWithChemicals
        FROM library.janitors
      """;

  private final ReactiveCassandraTemplate template;

  @Autowired
  public ReactiveCassandraTemplateJanitorRepository(ReactiveCassandraTemplate template) {
    this.template = template;
  }

  @Override
  public Flux<Janitor> findAll() {
    return template.query(Janitor.class).all();
  }

  @Override
  public Flux<JanitorChemicalView> findAllChemicalViews() {
    return template.select(SELECT_JANITOR_CHEMICAL_VIEWS, JanitorChemicalView.class);
  }

  @Override
  public Mono<Janitor> findById(UUID id) {
    return template.selectOneById(id, Janitor.class);
  }

  @Override
  public Flux<Janitor> findByCharacteristic(String characteristic) {
    return template.select(query(where("characteristic").like(characteristic)), Janitor.class);
  }

  @Override
  public Mono<Janitor> save(Janitor janitor) {
    var options = InsertOptions.builder()
        .consistencyLevel(ConsistencyLevel.ONE)
        .build();

    return template.insert(janitor, options)
        .map(EntityWriteResult::getEntity);
  }

  @Override
  public Mono<Janitor> update(Janitor janitor) {
    var options = UpdateOptions.builder()
        .consistencyLevel(ConsistencyLevel.ONE)
        .build();

    return template.update(janitor, options)
        .map(EntityWriteResult::getEntity);
  }

  @Override
  public Mono<Boolean> delete(Janitor janitor) {
    var options = DeleteOptions.builder()
        .consistencyLevel(ConsistencyLevel.ONE)
        .build();

    return template.delete(janitor, options)
        .map(WriteResult::wasApplied);
  }
}
