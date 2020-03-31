package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private Flux<String> intervalFlux;
  private final ObjectMapper mapper = new ObjectMapper();

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @PostConstruct
  private void setup() {
    intervalFlux = Flux.interval(Duration.ofSeconds(2)).map(it -> getMessage("silence"));
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {

    Mono<Void> send = session.send(Mono.just(session.textMessage(getMessage("starting"))));

    Mono<Void> sendMessages =
        session
            .send(intervalFlux.map(session::textMessage))
            .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());

    return send.then(sendMessages);
  }

  private Runnable sendAudioMessage(WebSocketSession session) {
    return () -> session.send(Mono.just(session.textMessage(getMessage("audio")))).subscribe();
  }

  private Runnable sendSilenceMessage(WebSocketSession session) {
    return () -> session.send(Mono.just(session.textMessage(getMessage("silence")))).subscribe();
  }

  private String getMessage(String message) {
    JsonNode node = mapper.valueToTree(new Event(message, Instant.now()));
    return node.toString();
  }
}
