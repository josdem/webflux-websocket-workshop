package com.jos.dem.webflux.websocket;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
	WebSocketClient webSocketClient(){
		return new ReactorNettyWebSocketClient();
	}

	@Bean
	CommandLineRunner run(WebSocketClient client){
		return args -> {
			client.execute(
					URI.create("ws://localhost:8080/channel"),
			session -> session.send(
					Mono.just(session.textMessage("event")))
					.thenMany(session.receive()
					.map(WebSocketMessage::getPayloadAsText)
					.log())
					.then())
					.block(Duration.ofSeconds(10L));
		};
	}

}
