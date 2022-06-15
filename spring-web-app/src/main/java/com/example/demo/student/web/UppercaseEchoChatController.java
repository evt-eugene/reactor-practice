package com.example.demo.student.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class UppercaseEchoChatController {

  @GetMapping
  public String chat() {
    return "chat.html";
  }
}
