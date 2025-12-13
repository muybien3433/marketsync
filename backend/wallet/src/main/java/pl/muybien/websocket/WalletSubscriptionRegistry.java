package pl.muybien.websocket;

import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import pl.muybien.enumeration.CurrencyType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class WalletSubscriptionRegistry {

    private final Map<String, WalletSubscription> subscriptions = new ConcurrentHashMap<>();

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        if (destination == null || sessionId == null) {
            return;
        }

        Object customerAttr = accessor.getSessionAttributes().get("customerId");
        if (!(customerAttr instanceof UUID customerId)) {
            return;
        }

        String[] parts = destination.split("/");
        if (parts.length < 4) {
            return;
        }

        try {
            CurrencyType currency = CurrencyType.valueOf(parts[3]);
            subscriptions.put(sessionId, new WalletSubscription(customerId, currency));
        } catch (Exception ignored) {
        }
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        subscriptions.remove(sessionId);
    }
}
