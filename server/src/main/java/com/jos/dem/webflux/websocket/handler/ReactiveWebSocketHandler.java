package com.jos.dem.webflux.websocket.handler;

import java.time.Duration;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.jos.dem.webflux.websocket.model.Event;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private static final ObjectMapper json = new ObjectMapper();

  private Flux<String> eventFlux = Flux.generate(sink -> {
    Event event = new Event(randomUUID().toString(), now().toString());
    try {
      sink.next(json.writeValueAsString(event));
    } catch (JsonProcessingException e) {
      sink.error(e);
    }
  });

  private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
    .zipWith(eventFlux, (time, event) -> event);

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return session.send(intervalFlux
        .map(session::textMessage))
      .and(session.receive()
          .map(WebSocketMessage::getPayloadAsText)
          .log());
  }
}
