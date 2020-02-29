package com.jos.dem.webflux.websocket;

import com.jos.dem.webflux.websocket.util.ApplicationState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class WebsocketClientApplicationTest {

  @Autowired private WebSocketClient client;

  @Autowired private WebSocketHandler webSocketHandler;

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Test
  @DisplayName("should subscribe to events")
  void shouldSubscribeToWebSocketServer() {
    client.execute(URI.create("ws://localhost:8080/channel"), webSocketHandler).block();

    assertFalse(ApplicationState.cache.isEmpty(), "should not be empty");
    ApplicationState.cache.forEach(event -> log.info("event: {}", event));
  }
}
