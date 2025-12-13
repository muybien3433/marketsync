package pl.muybien.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
public class CustomerIdHandshakeInterceptor implements HandshakeInterceptor {

    private static final String CUSTOMER_HEADER = "X-Customer-Id";

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        var headers = request.getHeaders();
        String customerIdHeader = headers.getFirst(CUSTOMER_HEADER);

        if (customerIdHeader != null && !customerIdHeader.isBlank()) {
            try {
                UUID customerId = UUID.fromString(customerIdHeader);
                attributes.put("customerId", customerId);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}
