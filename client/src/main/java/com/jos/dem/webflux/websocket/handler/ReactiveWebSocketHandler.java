package com.jos.dem.webflux.websocket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jos.dem.webflux.websocket.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

import java.time.Instant;

public class ReactiveWebSocketHandler implements WebSocketHandler {

  private WebSocketSessionHandler sessionHandler;
  private ReplayProcessor<WebSocketSessionHandler> connectedProcessor;
  private final ObjectMapper mapper = new ObjectMapper();

  private Logger log = LoggerFactory.getLogger(this.getClass());

  public ReactiveWebSocketHandler(){
    sessionHandler = new WebSocketSessionHandler();
    connectedProcessor = ReplayProcessor.create();
  }

  @Override
  public Mono<Void> handle(WebSocketSession session) {
    sessionHandler
            .connected()
            .doOnNext(value -> connectedProcessor.onNext(sessionHandler))
            .subscribe();

    return sessionHandler.handle(session);
  }

  private String getMessage() {
    JsonNode node = mapper.valueToTree(new Event("silence", Instant.now()));
    return node.toString();
  }

  public Flux<WebSocketSessionHandler> connected() {
    return connectedProcessor;
  }

}
