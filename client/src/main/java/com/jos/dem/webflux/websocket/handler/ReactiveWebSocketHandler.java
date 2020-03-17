package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private final ObjectMapper mapper = new ObjectMapper();

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public Mono<Void> handle(WebSocketSession session) {

    Mono<Void> send = session.send(Mono.just(session.textMessage(getMessage())));

    Flux<String> receive =
        session
            .receive()
            .map(message -> message.getPayloadAsText())
            .doOnNext(
                textMessage -> {
                  log.info("message: {}", textMessage);
                  if (textMessage.contains("Hola")) {
                    Mono.fromRunnable(sendMessage(session)).subscribe();
                  }
                })
            .doOnComplete(() -> log.info("complete"));

    return send.thenMany(receive).then();
  }

  private Runnable sendMessage(WebSocketSession session) {
    return () -> session.send(Mono.just(session.textMessage(getMessage()))).subscribe();
  }

  private String getMessage() {
    JsonNode node = mapper.valueToTree(new Event("silence", Instant.now()));
    return node.toString();
  }
}
