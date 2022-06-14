package com.example.demo.student.web;

import com.example.demo.student.entity.Greeting;
import com.example.demo.student.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/greeting")
public class GreetingController {

  private final GreetingService service;

  @Autowired
  public GreetingController(GreetingService service) {
    this.service = service;
  }

  @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Mono<Greeting> greeting() {
    return service.getGreeting();
  }

  @GetMapping(value = "/fetch", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public Mono<Greeting> fetchGreeting() {
    return service.fetchGreeting();
  }
}
