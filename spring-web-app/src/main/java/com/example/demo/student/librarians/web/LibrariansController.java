package com.example.demo.student.librarians.web;

import com.example.demo.student.librarians.entity.Librarian;
import com.example.demo.student.librarians.service.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
@RequestMapping("/librarians")
public class LibrariansController {

  private final LibrarianService service;

  @Autowired
  public LibrariansController(LibrarianService service) {
    this.service = service;
  }

  @GetMapping("/view")
  public String view(Model model) {
    var found = service.findAll();

    model.addAttribute("librarians", new ReactiveDataDriverContextVariable(found));

    return "librarians.html";
  }

  @GetMapping("/{id}")
  @ResponseBody
  public Mono<Librarian> getLibrarianById(@PathVariable("id") UUID id) {
    return service.getLibrarianById(id);
  }

  @PostMapping
  @ResponseBody
  public Mono<Librarian> addLibrarian(@RequestBody LibrarianDto dto) {
    return service.createLibrarian(dto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteLibrarian(@PathVariable("id") UUID id) {
    return service.deleteLibrarian(id);
  }
}
