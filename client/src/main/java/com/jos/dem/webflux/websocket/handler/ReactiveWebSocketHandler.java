package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import com.jos.dem.webflux.websocket.util.MessageGenerator;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private Flux<String> intervalFlux;
  private final MessageGenerator messageGenerator;
  private final ObjectMapper mapper = new ObjectMapper();

  @PostConstruct
  private void setup(){
    intervalFlux =
        Flux.interval(Duration.ofSeconds(1)).map(it -> getEvent());
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return session
        .send(intervalFlux.map(session::textMessage))
        .and(Mono.just(session.textMessage(getEvent())))
        .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());
  }

  private String getEvent(){
    JsonNode node = mapper.valueToTree(new Event("start", Instant.now()));
    return node.toString();
  }

  private String getStream(){
    JsonNode node = mapper.valueToTree(new Event(messageGenerator.generate(), Instant.now()));
    return node.toString();
  }
}
