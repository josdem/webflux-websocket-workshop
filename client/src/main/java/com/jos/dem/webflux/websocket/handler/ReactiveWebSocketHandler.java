package com.jos.dem.webflux.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return session
        .send(Mono.just(session.textMessage("event")))
        .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());
  }
}
