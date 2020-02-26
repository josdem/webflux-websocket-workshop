package com.jos.dem.webflux.websocket.handler;

import com.jos.dem.webflux.websocket.util.MessageGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

  @Autowired private MessageGenerator messageGenerator;

  private Flux<String> intervalFlux =
      Flux.interval(Duration.ofSeconds(1)).map(it -> messageGenerator.generate());

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return session
        .send(intervalFlux.map(session::textMessage))
        .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());
  }
}
