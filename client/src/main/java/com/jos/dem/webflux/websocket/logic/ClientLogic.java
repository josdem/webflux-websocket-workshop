package com.jos.dem.webflux.websocket.logic;

import com.jos.dem.webflux.websocket.handler.ReactiveWebSocketHandler;
import com.jos.dem.webflux.websocket.handler.WebSocketSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

public class ClientLogic {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Disposable start(WebSocketClient webSocketClient, URI uri, ReactiveWebSocketHandler clientWebSocketHandler)
    {
        clientWebSocketHandler
                .connected()
                .subscribe(this::doLogic);

        Disposable clientConnection =
                webSocketClient
                        .execute(uri, clientWebSocketHandler)
                        .subscribeOn(Schedulers.elastic())
                        .subscribe();

        return clientConnection;
    }

    private void doLogic(WebSocketSessionHandler sessionHandler)
    {
        sessionHandler
                .connected()
                .doOnNext(session -> logger.info("Client Connected [{}]", session.getId()))
                .map(session -> "Test Message")
                .doOnNext(message -> sessionHandler.send(message))
                .subscribe(message -> logger.info("Client Sent: [{}]", message));

        sessionHandler
                .disconnected()
                .subscribe(session -> logger.info("Client Disconnected [{}]", session.getId()));

        sessionHandler
                .receive()
                .subscribeOn(Schedulers.elastic())
                .subscribe(message -> logger.info("Client Received: [{}]", message));
    }
}
