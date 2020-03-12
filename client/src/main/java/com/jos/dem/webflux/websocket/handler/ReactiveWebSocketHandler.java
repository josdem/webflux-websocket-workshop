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

  private Flux<String> emiterFlux;
  private Flux<String> intervalNameFlux;
  private Flux<String> intervalSilenceFlux;
  private final MessageGenerator messageGenerator;
  private final ObjectMapper mapper = new ObjectMapper();

  @PostConstruct
  private void setup(){
    intervalNameFlux =
        Flux.interval(Duration.ofSeconds(1)).map(it -> getSilence());
    intervalSilenceFlux =
        Flux.interval(Duration.ofSeconds(1)).map(it -> getStream());
    emiterFlux = intervalNameFlux;
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    return session
        .send(emiterFlux.map(session::textMessage))
        .and(session.receive().map(message -> message.getPayloadAsText()).log());
  }

  private String getSilence(){
    JsonNode node = mapper.valueToTree(new Event("silence", Instant.now()));
    return node.toString();
  }

  private String getStream(){
    JsonNode node = mapper.valueToTree(new Event(messageGenerator.generate(), Instant.now()));
    return node.toString();
  }
}
