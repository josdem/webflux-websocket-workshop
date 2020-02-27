package com.jos.dem.webflux.websocket;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class WebsocketClientApplication {

  private Logger log = LoggerFactory.getLogger(this.getClass());

  public static void main(String[] args) {
    SpringApplication.run(WebsocketClientApplication.class, args);
  }

  @Bean
  CommandLineRunner run(WebSocketClient client, WebSocketHandler webSocketHandler) {
    return args -> {
      Mono<Void> subscriber = client.execute(URI.create("ws://localhost:8080/channel"), webSocketHandler);

      subscriber.subscribe(
          content -> log.info("content"),
          error -> log.error("Error at receiving events: {}", error),
          () -> log.info("complete"));
      };
  }
}
