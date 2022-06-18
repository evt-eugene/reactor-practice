package com.example.demo.student.web;

import com.example.demo.student.entity.Book;
import com.example.demo.student.service.BooksService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.net.URI;

@Controller
@RequestMapping("/books")
public class BooksController {

  private final ReplayProcessor<Book> lastTwoCached = ReplayProcessor.create(2);
  private final BooksService service;

  public BooksController(BooksService service) {
    this.service = service;

    service.findAll()
        .subscribe(lastTwoCached);
  }

  @GetMapping
  @ResponseBody
  public Flux<Book> findAll() {
    return service.findAll();
  }

  @GetMapping("/cached")
  @ResponseBody
  public Flux<Book> findAllCached() {
    return lastTwoCached;
  }

  @GetMapping("/add")
  public String bookAddFormUsingTransferEncodingChunked(Model model) {
    var found = service.findByTitle("ddd");

    model.addAttribute("booksWithDDDTitle", new ReactiveDataDriverContextVariable(found));

    return "book-form.html";
  }

  @GetMapping("/add/reactively")
  public Mono<String> bookAddForm(Model model) {
    return service.findByTitle("ddd")
        .collectList()
        .doOnNext(books -> model.addAttribute("booksWithDDDTitle", books))
        .then(Mono.just("book-form.html"));
  }

  @PostMapping
  public Mono<ResponseEntity<Book>> createBook(@RequestBody BookTitleDto titleDto) {
    return service.createBook(titleDto)
        .map(b ->
                 ResponseEntity.created(URI.create("/books/" + b.getId()))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(b)
        )
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }

  @GetMapping("/booksWithDDDTitle")
  public String showBooksForUpdateByTitle(Model model) {
    var all = service.findByTitle("ddd");

    model.addAttribute("booksWithDDDTitle", new ReactiveDataDriverContextVariable(all));

    return "update-year-by-title-form.html";
  }

  @GetMapping(params = "title")
  @ResponseBody
  public Flux<Book> booksByTitle(@RequestParam("title") String title) {
    return service.findByTitle(title);
  }

  @PutMapping(params = "title")
  @ResponseBody
  public Flux<Book> updateBookPublishingYearByTitle(
      @RequestParam("title") String title,
      @RequestBody BookPublishingYearDto yearDto
  ) {
    return service.updateBookPublishingYearByTitle(title, yearDto);
  }
}
