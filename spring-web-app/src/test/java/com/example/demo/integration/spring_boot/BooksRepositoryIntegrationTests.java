package com.example.demo.integration.spring_boot;

import com.datastax.driver.core.utils.UUIDs;
import com.example.demo.student.entity.Book;
import com.example.demo.student.persistance.ReactiveSpringDataCassandraBookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@DataCassandraTest
@Testcontainers
@DirtiesContext
public class BooksRepositoryIntegrationTests {

  @Container
  private static final CassandraContainer<?> cassandraContainer = new CassandraContainer<>("cassandra:4.0.4")
      .withInitScript("cql/test-db-schema.cql")
      .withExposedPorts(9042)
      .withNetwork(Network.newNetwork());

  @DynamicPropertySource
  private static void bindCassandraProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.cassandra.keyspace-name", () -> "library");
    registry.add("spring.data.cassandra.contact-points", cassandraContainer::getHost);
    registry.add("spring.data.cassandra.port", () -> cassandraContainer.getMappedPort(9042));
  }

  @Autowired
  private ReactiveSpringDataCassandraBookRepository bookRepository;

  @Test
  public void testFindByTitle() {
    var book = new Book(UUIDs.timeBased(), "Harry Potter", 2001);

    bookRepository.save(book)
        .thenMany(bookRepository.findByTitle("Harry Potter"))
        .as(StepVerifier::create)
        .expectNextMatches(b -> b.getTitle().equals("Harry Potter"))
        .expectComplete()
        .verify();
  }
}
