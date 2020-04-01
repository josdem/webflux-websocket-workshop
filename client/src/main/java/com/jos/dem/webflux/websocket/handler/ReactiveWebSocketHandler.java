package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import com.jos.dem.webflux.websocket.model.Person;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private Flux<Person> personStream;
  private final ObjectMapper mapper = new ObjectMapper();

  @PostConstruct
  private void setup() {
    List<Person> persons =
        Arrays.asList(
            new Person("josdem", "josdem@email.com"),
            new Person("skye", "skye@email.com"),
            new Person("tgrip", "tgrip@email.com"),
            new Person("edzero", "edzero@email.com"),
            new Person("jeduan", "jeduan@email.com"));

    personStream = Flux.fromIterable(persons).delayElements(Duration.ofSeconds(1));
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {

    Mono<Void> send = session.send(Mono.just(session.textMessage(getMessage("starting"))));

    Mono<Void> sendMessages =
        session
            .send(personStream.map(message -> session.textMessage(message.toString())))
            .and(session.receive().map(WebSocketMessage::getPayloadAsText).log());

    return send.then(sendMessages);
  }

  private String getMessage(String message) {
    JsonNode node = mapper.valueToTree(new Event(message, Instant.now()));
    return node.toString();
  }
}
