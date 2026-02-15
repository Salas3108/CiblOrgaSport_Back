package com.ciblorgasport.realtimegateway.kafka;

import com.ciblorgasport.realtimegateway.client.AbonnementServiceClient;
import com.ciblorgasport.realtimegateway.kafka.dto.NotificationEvent;
import com.ciblorgasport.realtimegateway.kafka.dto.TargetType;
import com.ciblorgasport.realtimegateway.ws.ConnectedUserRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class RealtimeNotificationService {

    private final ObjectMapper objectMapper;
    private final ConnectedUserRegistry connectedUserRegistry;
    private final AbonnementServiceClient abonnementServiceClient;

    public RealtimeNotificationService(ObjectMapper objectMapper,
                                       ConnectedUserRegistry connectedUserRegistry,
                                       AbonnementServiceClient abonnementServiceClient) {
        this.objectMapper = objectMapper;
        this.connectedUserRegistry = connectedUserRegistry;
        this.abonnementServiceClient = abonnementServiceClient;
    }

    @KafkaListener(topics = "${ciblorgasport.kafka.topics.notifications}")
    public void onNotification(String payload) throws Exception {
        NotificationEvent event = objectMapper.readValue(payload, NotificationEvent.class);
        route(event);
    }

    private void route(NotificationEvent event) throws Exception {
        if (event == null || event.getTargetType() == null) return;

        String json = objectMapper.writeValueAsString(event);
        TextMessage msg = new TextMessage(json);

        if (event.getTargetType() == TargetType.USER) {
            if (event.getTargetUserId() == null) return;
            sendToUser(event.getTargetUserId(), msg);
            return;
        }

        if (event.getTargetType() == TargetType.COMPETITION_SUBSCRIBERS) {
            List<Long> userIds = abonnementServiceClient.getSubscriberUserIds(event.getTargetCompetitionId());
            for (Long userId : userIds) {
                sendToUser(userId, msg);
            }
            return;
        }

        if (event.getTargetType() == TargetType.BROADCAST) {
            for (WebSocketSession session : connectedUserRegistry.allSessions()) {
                safeSend(session, msg);
            }
        }
    }

    private void sendToUser(Long userId, TextMessage msg) {
        WebSocketSession session = connectedUserRegistry.getSession(userId);
        safeSend(session, msg);
    }

    private void safeSend(WebSocketSession session, TextMessage msg) {
        if (session == null) return;
        if (!session.isOpen()) return;
        try {
            session.sendMessage(msg);
        } catch (Exception ignored) {
        }
    }
}
