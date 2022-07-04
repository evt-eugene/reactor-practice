package com.example.demo.student.service.impl;

import com.example.demo.student.entity.Janitor;
import com.example.demo.student.service.JanitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import reactor.core.publisher.Flux;

public class CassandraTemplateJanitorService implements JanitorService {

  private final CassandraTemplate template;

  @Autowired
  public CassandraTemplateJanitorService(CassandraTemplate template) {
    this.template = template;
  }

  @Override
  public Flux<Janitor> findAll() {
    return template.select(Query.query(Criteria.where("id").is("1")), Janitor.class).;
  }
}
