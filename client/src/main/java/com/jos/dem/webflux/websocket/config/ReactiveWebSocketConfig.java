package com.jos.dem.webflux.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

@Configuration
public class ReactiveWebSocketConfig {

  @Bean
  WebSocketClient webSocketClient() {
    return new ReactorNettyWebSocketClient();
  }
}
