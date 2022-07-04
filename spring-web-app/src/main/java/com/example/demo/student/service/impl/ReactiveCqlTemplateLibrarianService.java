package com.example.demo.student.service.impl;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.demo.student.entity.librarian.FullName;
import com.example.demo.student.entity.librarian.Librarian;
import com.example.demo.student.service.LibrarianService;
import com.example.demo.student.web.LibrarianDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate;
import org.springframework.data.cassandra.core.cql.RowMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ReactiveCqlTemplateLibrarianService implements LibrarianService {

  private static final String SELECT_ALL = "SELECT id, name, age, version FROM library.librarians";
  private static final String SELECT_ONE_BY_ID = "SELECT id, name, age, version FROM library.librarians WHERE id=?";
  private static final String INSERT_ONE = "INSERT INTO library.librarians (id, name, age, version)"
                                               + " VALUES (?, {firstname: ?, lastname: ?, middlename: ?}, ?, 0)";
  private static final String DELETE_ONE = "DELETE FROM library.librarians WHERE id=?";

  private final ReactiveCqlTemplate cqlTemplate;

  @Autowired
  public ReactiveCqlTemplateLibrarianService(ReactiveCqlTemplate cqlTemplate) {
    this.cqlTemplate = cqlTemplate;
  }

  @Override
  public Flux<Librarian> findAll() {
    return cqlTemplate.query(SELECT_ALL, ROW_MAPPER);
  }

  @Override
  public Mono<Librarian> createLibrarian(LibrarianDto dto) {
    return Mono.defer(() -> Mono.just(Uuids.timeBased()))
        .flatMap(id -> cqlTemplate
            .execute(INSERT_ONE, id, dto.getFirstName(), dto.getLastName(), dto.getMiddleName(), dto.getAge())
            .flatMap(wasApplied -> cqlTemplate.queryForObject(SELECT_ONE_BY_ID, ROW_MAPPER, id)));
  }

  @Override
  public Mono<Void> deleteLibrarian(UUID id) {
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
