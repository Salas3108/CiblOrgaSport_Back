package com.ciblorgasport.realtimegateway.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class RealtimeWebSocketHandler extends TextWebSocketHandler {

    private final ConnectedUserRegistry connectedUserRegistry;

    public RealtimeWebSocketHandler(ConnectedUserRegistry connectedUserRegistry) {
        this.connectedUserRegistry = connectedUserRegistry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Object userIdAttr = session.getAttributes().get(JwtHandshakeInterceptor.ATTR_USER_ID);
        if (!(userIdAttr instanceof Long userId)) {
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE);
            } catch (Exception ignored) {
            }
            return;
        }
        connectedUserRegistry.register(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Object userIdAttr = session.getAttributes().get(JwtHandshakeInterceptor.ATTR_USER_ID);
        if (userIdAttr instanceof Long userId) {
            connectedUserRegistry.unregister(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // No client -> server messages expected for now.
    }
}
