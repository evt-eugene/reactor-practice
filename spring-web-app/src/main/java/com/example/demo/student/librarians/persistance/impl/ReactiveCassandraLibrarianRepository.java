package com.example.demo.student.librarians.persistance.impl;

import com.example.demo.student.librarians.entity.FullName;
import com.example.demo.student.librarians.entity.Librarian;
import com.example.demo.student.librarians.persistance.LibrarianRepository;
import com.example.demo.student.librarians.web.LibrarianDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate;
import org.springframework.data.cassandra.core.cql.RowMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class ReactiveCassandraLibrarianRepository implements LibrarianRepository {

  private static final String SELECT_ALL =
      "SELECT id, name, age, version FROM library.librarians";

  private static final String SELECT_ONE_BY_ID =
      "SELECT id, name, age, version FROM library.librarians WHERE id=?";

  private static final String INSERT_ONE =
      "INSERT INTO library.librarians (id, name, age, version) VALUES (?, {firstname: ?, lastname: ?, middlename: ?}, ?, 0)";

  private static final String DELETE_ONE =
      "DELETE FROM library.librarians WHERE id=?";

  private final ReactiveCqlTemplate cqlTemplate;

  @Autowired
  public ReactiveCassandraLibrarianRepository(ReactiveCqlTemplate cqlTemplate) {
    this.cqlTemplate = cqlTemplate;
  }

  @Override
  public Flux<Librarian> findAll() {
    return cqlTemplate.query(SELECT_ALL, ROW_MAPPER);
  }

  @Override
  public Mono<Librarian> findLibrarianById(UUID id) {
    return cqlTemplate.queryForObject(SELECT_ONE_BY_ID, ROW_MAPPER, id);
  }

  @Override
  public Mono<Librarian> insert(UUID id, LibrarianDto dto) {
    return cqlTemplate.execute(INSERT_ONE, id, dto.getFirstName(), dto.getLastName(), dto.getMiddleName(), dto.getAge())
        .flatMap(wasApplied -> getLibrarianByIdWithCustomCreators(id));
  }

  private Mono<Librarian> getLibrarianByIdWithCustomCreators(UUID id) {
    return cqlTemplate.query(session -> session.prepare(SELECT_ONE_BY_ID), ps -> ps.bind().setUuid(0, id), ROW_MAPPER)
        .buffer(1)
        .flatMap(list -> Mono.just(DataAccessUtils.requiredSingleResult(list)))
        .next();
  }

  @Override
  public Mono<Void> deleteById(UUID id) {
    return cqlTemplate.execute(DELETE_ONE, id)
        .flatMap(wasApplied -> !wasApplied ? Mono.error(new RuntimeException("The query was not applied")) : Mono.empty())
        .then();
  }

  private static final RowMapper<Librarian> ROW_MAPPER = (row, rowNum) -> {
    var id = row.getUuid("id");
    var nameUdt = row.getUdtValue("name");
    var age = row.getByte("age");
    var version = row.getLong("version");

    var firstName = nameUdt.getString("firstName");
    var lastName = nameUdt.getString("lastName");
    var middleName = nameUdt.getString("middleName");

    var fullName = new FullName(firstName, lastName, middleName);
    return new Librarian(id, fullName, age, version);
  };
}
