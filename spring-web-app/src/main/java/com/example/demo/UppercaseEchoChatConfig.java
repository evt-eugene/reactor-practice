package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;

import java.util.Map;

@Configuration
public class UppercaseEchoChatConfig {

  @Bean
  public HandlerMapping handlerMapping() {
    var map = Map.of("/echo", (WebSocketHandler) session -> {
      var input = session.receive()
          .map(WebSocketMessage::getPayloadAsText)
          .map(String::toUpperCase)
          .map(session::textMessage);

      return session.send(input);
    });

    return new SimpleUrlHandlerMapping(map, -1);
  }
}
