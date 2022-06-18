package com.example.demo.integration.spring_boot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UppercaseEchoChatWebSocketAPITests {

  @Test
  @WithMockUser
  public void checkThatUserIsAbleToMakeATrade() {
//    URI uri = URI.create("ws://localhost:8080/echo");
//
//    ReactorNettyWebSocketClient client = ReactorNettyWebSocketClient.create(uri);
//    ReactorNettyWebSocketClient
//    TestPublisher<String> testPublisher = TestPublisher.create();
//    Flux<String> inbound = testPublisher
//        .flux()
//        .subscribeWith(ReplayProcessor.create(1))
//        .transform(client::sendAndReceive)
//        .map(WebSocketMessage::getPayloadAsText);
//
//    StepVerifier
//        .create(inbound)
//        .expectSubscription()
//        .then(() -> testPublisher.next("TRADES|BTC"))
//        .expectNext("PRICE|AMOUNT|CURRENCY")
//        .then(() -> testPublisher.next("TRADE: 10123|1.54|BTC"))
//        .expectNext("10123|1.54|BTC")
//        .then(() -> testPublisher.next("TRADE: 10090|-0.01|BTC"))
//        .expectNext("10090|-0.01|BTC")
//        .thenCancel()
//        .verify();
  }

}
