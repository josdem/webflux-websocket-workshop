package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import com.jos.dem.webflux.websocket.util.MessageGenerator;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.publisher.ReplayProcessor;

@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private Flux<String> intervalNameFlux;
  private Flux<String> intervalSilenceFlux;
  private final MessageGenerator messageGenerator;
  private final ObjectMapper mapper = new ObjectMapper();

  private Logger log = LoggerFactory.getLogger(this.getClass());

  @PostConstruct
  private void setup() {
    intervalSilenceFlux = Flux.interval(Duration.ofSeconds(1)).map(it -> getSilence());
    intervalNameFlux = Flux.interval(Duration.ofSeconds(1)).map(it -> getStream());
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    Flux<String> receive =
        session
            .receive()
            .map(message -> message.getPayloadAsText())
            .doOnNext(
                textMessage -> {
                  log.info("message: {}", textMessage);
                  if (textMessage.contains("Hola")) {
                    log.info("Hola");
                    Mono.fromRunnable(() -> send(session));
                  }
                })
            .doOnComplete(() -> log.info("complete"));

    return send(session).thenMany(receive).then();
  }

  private Mono<Void> send(WebSocketSession session) {
    return session.send(Mono.just(session.textMessage(getSilence())));
  }

  private String getSilence() {
    JsonNode node = mapper.valueToTree(new Event("silence", Instant.now()));
    return node.toString();
  }

  private String getStream() {
    JsonNode node = mapper.valueToTree(new Event(messageGenerator.generate(), Instant.now()));
    return node.toString();
  }
}
