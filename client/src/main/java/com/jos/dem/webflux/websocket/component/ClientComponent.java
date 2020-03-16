package com.jos.dem.webflux.websocket.component;

import com.jos.dem.webflux.websocket.handler.ReactiveWebSocketHandler;
import com.jos.dem.webflux.websocket.logic.ClientLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;

@Component
public class ClientComponent implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ConfigurableApplicationContext applicationContext;
    @Autowired
    private WebSocketClient webSocketClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ClientLogic clientLogic = new ClientLogic();
        Disposable logicOne = clientLogic.start(webSocketClient, URI.create("ws://localhost:8080/channel"), new ReactiveWebSocketHandler());

        Mono
                .delay(Duration.ofSeconds(1000))
                .doOnEach(value -> logicOne.dispose())
                .map(value -> SpringApplication.exit(applicationContext, () -> 0))
                .subscribe(exitValue -> System.exit(exitValue));
    }
}
