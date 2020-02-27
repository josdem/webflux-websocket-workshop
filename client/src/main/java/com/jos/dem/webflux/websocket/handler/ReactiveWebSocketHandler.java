package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return session
        .send(Mono.just(session.textMessage(getEvent())))
        .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());
  }

  private String getEvent(){
    JsonNode node = mapper.valueToTree(new Event("start", Instant.now()));
    return node.toString();
  }
}
