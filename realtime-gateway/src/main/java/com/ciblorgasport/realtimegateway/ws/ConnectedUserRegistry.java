package com.ciblorgasport.realtimegateway.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectedUserRegistry {

    private final Map<Long, WebSocketSession> sessionsByUserId = new ConcurrentHashMap<>();

    public void register(Long userId, WebSocketSession session) {
        sessionsByUserId.put(userId, session);
    }

    public void unregister(Long userId, WebSocketSession session) {
        WebSocketSession current = sessionsByUserId.get(userId);
        if (current != null && current.getId().equals(session.getId())) {
            sessionsByUserId.remove(userId);
        }
    }

    public WebSocketSession getSession(Long userId) {
        return sessionsByUserId.get(userId);
    }

    public Collection<WebSocketSession> allSessions() {
        return sessionsByUserId.values();
    }
}
