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

import java.net.URI;

@Controller
@RequestMapping("/books")
public class BooksController {

  private final BooksService service;

  public BooksController(BooksService service) {
    this.service = service;
  }

  @GetMapping
  @ResponseBody
  public Flux<Book> findAll() {
    return service.findAll();
  }

  @GetMapping("/add")
  public String bookAddFormGood(Model model) {
    var found = service.findByTitle("ddd");

    model.addAttribute("booksByDDDTitle", new ReactiveDataDriverContextVariable(found));

    return "book-form.html";
  }

  @GetMapping("/add/reactively")
  public Mono<String> bookAddFormReactivelyBad(Model model) {
    return service.findByTitle("ddd")
        .collectList()
        .doOnNext(books -> model.addAttribute("booksByDDDTitle", books))
        .then(Mono.just("book-form.html"));
  }

  @PostMapping
  public Mono<ResponseEntity<Book>> createBook(@RequestBody BookDto bookDto) {
    return service.createBook(bookDto)
        .map(b ->
                 ResponseEntity.created(URI.create("/books/" + b.getId()))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(b)
        )
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }
}
