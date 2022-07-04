package com.example.demo.integration;

import com.datastax.driver.core.utils.UUIDs;
import com.example.demo.student.entity.book.Book;
import com.example.demo.student.persistence.ReactiveSpringDataCassandraBookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class BooksControllerIntegrationTestCase {

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

  @Autowired
  private WebTestClient webTestClient;

  @Test
  public void testFindByTitle() {
    var book = new Book(UUIDs.timeBased(), "Harry Potter", 2001);

    bookRepository.save(book)
        .then()
        .block();


    webTestClient
        .get().uri("/books?title=Harry Potter")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$[0].id").value(equalTo(String.valueOf(book.getId())))
        .jsonPath("$[0].title").value(equalTo("Harry Potter"));
  }
}
