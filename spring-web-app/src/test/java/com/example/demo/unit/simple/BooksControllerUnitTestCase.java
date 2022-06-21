package com.example.demo.unit.simple;

import com.example.demo.student.entity.Book;
import com.example.demo.student.service.BooksService;
import com.example.demo.student.web.BooksController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * This testcase doesn't use @InjectMocks because the constructor of BookController performs service.findAll() operation
 * before providing values for the service.findAll()-call
 * In other case @InjectMocks and Mockito.when is a better choice!
 */
@ExtendWith(MockitoExtension.class)
public class BooksControllerUnitTestCase {

  @Mock
  private BooksService service;

  @Test
  public void verifyRespondWithExpectedBooks() {
    var expectedBook = new Book(UUID.randomUUID(), "Harry Potter", 2000);

    Mockito.when(service.findAll())
        .thenReturn(Flux.just(expectedBook));

    BooksController controller = new BooksController(service);

    WebTestClient
        .bindToController(controller)
        .build()
        .get().uri("/books")
        .exchange()
        .expectHeader().contentTypeCompatibleWith(APPLICATION_JSON)
        .expectStatus().is2xxSuccessful()
        .returnResult(Book.class)
        .getResponseBody().as(StepVerifier::create)
        .expectNextMatches(b -> b.getTitle().equals("Harry Potter") && b.getPublishingYear() == 2000)
        .expectNextCount(0)
        .expectComplete()
        .verify();

    verify(service, times(2)).findAll();
  }
}
