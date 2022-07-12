package com.example.demo.student.janitors.persistance;

import com.example.demo.student.janitors.entity.Janitor;
import com.example.demo.student.janitors.entity.projections.JanitorChemicalView;
import com.example.demo.student.janitors.entity.projections.TotalJanitorChemicalView;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoRepositoryBean
public interface JanitorRepository extends Repository<Janitor, UUID> {

  Flux<Janitor> findAll();

  Flux<JanitorChemicalView> findAllChemicalViews();

  Mono<TotalJanitorChemicalView> getTotalChemicalView();

  Mono<Janitor> findById(UUID id);

  Flux<Janitor> findByCharacteristic(String characteristic);

  Mono<Janitor> save(Janitor janitor);

  Mono<Janitor> update(Janitor janitor);

  Mono<Boolean> delete(Janitor janitor);

}
