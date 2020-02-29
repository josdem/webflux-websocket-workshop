package com.jos.dem.webflux.websocket;

import com.jos.dem.webflux.websocket.util.ApplicationState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WebsocketClientApplicationTest {

  @Autowired private WebSocketClient client;

  @Autowired
  private WebSocketHandler webSocketHandler;

  private Mono<Void> subscriber;

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @BeforeAll
  void setup() {
    subscriber = client.execute(URI.create("ws://localhost:8080/channel"), webSocketHandler);
  }

  @Test
  @DisplayName("should subscribe to events")
  void shouldSubscribeToWebSocketServer() {
    subscriber.subscribe(
        content -> log.info("content"),
        error -> log.error("Error at receiving events: {}", error),
        () -> validate());
  }

  private void validate() {
    assertFalse(ApplicationState.cache.isEmpty(), "should not be empty");
  }
}
