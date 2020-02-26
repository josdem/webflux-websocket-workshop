package com.jos.dem.webflux.websocket;

import java.net.URI;
import java.time.Duration;

import com.jos.dem.webflux.websocket.handler.ReactiveWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class WebsocketClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketClientApplication.class, args);
    }

    @Bean
    WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Bean
    CommandLineRunner run(WebSocketClient client) {
        return args -> {
            client.execute(
                    URI.create("ws://localhost:8080/channel"),
                    webSocketHandler).block();
        };
    }

}
