package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import com.jos.dem.webflux.websocket.util.MessageGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private Flux<String> intervalFlux;
  private final ObjectMapper mapper = new ObjectMapper();
  private final MessageGenerator messageGenerator;

  @PostConstruct
  private void setup() {
    intervalFlux = Flux.interval(Duration.ofSeconds(2)).map(it -> getEvent());
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    Mono<Void> receive = session.send(
        session
            .receive()
            .map(webSocketMessage -> webSocketMessage.getPayloadAsText())
            .log()
            .map(message -> session.textMessage(message)));

    Mono<Void> send = session
            .send(intervalFlux.map(session::textMessage))
            .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());

    return receive.then(send);
  }

  private String getEvent() {
    JsonNode node = mapper.valueToTree(new Event(messageGenerator.generate(), Instant.now()));
    return node.toString();
  }
}
