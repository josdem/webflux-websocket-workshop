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
import reactor.core.publisher.MonoProcessor;
import reactor.core.publisher.ReplayProcessor;

@Component
@RequiredArgsConstructor
public class ReactiveWebSocketHandler implements WebSocketHandler {

  private Flux<String> emiterFlux;
  private Flux<String> intervalNameFlux;
  private Flux<String> intervalSilenceFlux;
  private final MessageGenerator messageGenerator;
  private final ObjectMapper mapper = new ObjectMapper();
  private MonoProcessor<WebSocketSession> connectedProcessor;
  private WebSocketSession session;
  private ReplayProcessor<String> receiveProcessor;
  private boolean webSocketConnected = false;

  @PostConstruct
  private void setup(){
    intervalNameFlux =
        Flux.interval(Duration.ofSeconds(1)).map(it -> getSilence());
    intervalSilenceFlux =
        Flux.interval(Duration.ofSeconds(1)).map(it -> getStream());
    emiterFlux = intervalNameFlux;
    connectedProcessor = MonoProcessor.create();
    receiveProcessor = ReplayProcessor.create(50);
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    this.session = session;

    Mono<Object> connected =
        Mono
            .fromRunnable(() ->
            {
              webSocketConnected = true;
              connectedProcessor.onNext(session);
            });

    Mono<Void> send =
        session
          .send(Mono.just(session.textMessage(getSilence())));

    Flux<String> receive =
        session
            .receive()
            .map(message -> message.getPayloadAsText())
            .doOnNext(textMessage -> receiveProcessor.onNext(textMessage))
            .doOnComplete(() -> receiveProcessor.onComplete());

    return connected.then(send).thenMany(receive).then();
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
