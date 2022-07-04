package com.example.demo.unit.webflux;

import com.example.demo.student.entity.book.Book;
import com.example.demo.student.service.BooksService;
import com.example.demo.student.web.BookPublishingYearDto;
import com.example.demo.student.web.BooksController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * This test doesn't use @MockBean because the constructor of BookController performs service.findAll() operation
 * before providing values for the service.findAll()-call.
 * In other case @MockBean and Mockito.when is a better choice!
 */
@WebFluxTest(BooksController.class)
@ImportAutoConfiguration({
    BooksControllerUnitTestCase.BookServiceConfig.class
})
public class BooksControllerUnitTestCase {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  @WithMockUser
  public void testAll() {
    webTestClient
        .get().uri("/books")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Book.class)
        .getResponseBody().as(StepVerifier::create)
        .expectNextMatches(book -> book.getTitle().equals("Harry Potter and the Philosopher's Stone"))
        .expectNextMatches(book -> book.getTitle().equals("Thing explainer"))
        .expectComplete()
        .verify();
  }

  @Test
  @WithMockUser
  public void testByTitle() {
    webTestClient
        .get().uri("/books?title=Thing explainer")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Book.class)
        .getResponseBody().as(StepVerifier::create)
        .expectNextMatches(book -> book.getTitle().equals("Thing explainer"))
        .expectComplete()
        .verify();
  }

  @Test
  @WithMockUser
  public void testUpdatePublishingYearByTitle() {
    webTestClient
        .mutateWith(csrf())
        .put().uri("/books?title=Thing explainer")
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new BookPublishingYearDto(2021)))
        .exchange()
        .expectStatus().isOk()
        .returnResult(Book.class)
        .getResponseBody().as(StepVerifier::create)
        .expectNextMatches(book -> book.getTitle().equals("Thing explainer") && book.getPublishingYear() == 2021)
        .expectComplete()
        .verify();
  }

  @TestConfiguration
  public static class BookServiceConfig {

    private static Book makeBook(String title, int year) {
      return new Book(UUID.randomUUID(), title, year);
    }

    @Bean
    public BooksService booksService() {
      var booksService = Mockito.mock(BooksService.class);

      var harryPotter = makeBook("Harry Potter and the Philosopher's Stone", 2001);
      var thingExplainer = makeBook("Thing explainer", 2020);

      Mockito.when(booksService.findAll())
          .thenReturn(Flux.just(harryPotter, thingExplainer));

      Mockito.when(booksService.findByTitle("Thing explainer"))
          .thenReturn(Flux.just(thingExplainer));

      Mockito.when(booksService.updateBookPublishingYearByTitle(
              eq("Thing explainer"),
              argThat(dto -> dto.getPublishingYear() == 2021))
          )
          .thenReturn(Flux.just(makeBook("Thing explainer", 2021)));

      return booksService;
    }
  }

}
