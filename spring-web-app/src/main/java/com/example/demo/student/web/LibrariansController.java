package com.example.demo.student.web;

import com.example.demo.student.entity.Librarian;
import com.example.demo.student.service.LibrarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

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

  @PostMapping
  @ResponseBody
  public Mono<Librarian> addLibrarian(@RequestBody LibrarianDto dto) {
    return service.createLibrarian(dto);
  }
}
