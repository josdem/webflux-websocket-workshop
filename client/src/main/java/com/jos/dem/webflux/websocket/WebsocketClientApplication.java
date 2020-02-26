package com.jos.dem.webflux.websocket;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;

@SpringBootApplication
public class WebsocketClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebsocketClientApplication.class, args);
  }

  @Bean
  CommandLineRunner run(WebSocketClient client, WebSocketHandler webSocketHandler) {
    return args -> {
      client.execute(URI.create("ws://localhost:8080/channel"), webSocketHandler).block();
    };
  }
}
