package com.example.demo.unit.integration;

import com.datastax.driver.core.utils.UUIDs;
import com.example.demo.student.entity.Book;
import com.example.demo.student.persistence.ReactiveSpringDataCassandraBookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Testcontainers
public class BooksRepositoryIntegrationTestCase {

  @Container
  private static final CassandraContainer<?> cassandraContainer = new CassandraContainer<>("cassandra:4.0.4")
      .withExposedPorts(9042)
      .withNetwork(Network.newNetwork());

  @DynamicPropertySource
  private static void bindCassandraProperties(DynamicPropertyRegistry registry) {
    registry.add("cassandra.contact-points", cassandraContainer::getHost);
    registry.add("cassandra.port", () -> cassandraContainer.getMappedPort(9042));
    registry.add("cassandra.local-datacenter", () -> "datacenter1");
    registry.add("cassandra.keyspace-name", () -> "library");
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
