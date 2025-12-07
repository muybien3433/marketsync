package pl.muybien.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pl.muybien.exception.CustomerNotFoundException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final ReactiveJwtDecoder jwtDecoder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is invalid");
        }

        return extractCustomerFromHeader(authHeader)
                .flatMap(customer -> {
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .headers(headers -> {
                                headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                                headers.set("X-Customer-Id", customer.id());
                                headers.set("X-Customer-Email", customer.email());
                                headers.set("X-Customer-Number", customer.number());
                                headers.set("X-Customer-FirstName", customer.firstName());
                                headers.set("X-Customer-LastName", customer.lastName());
                                headers.set("X-Customer-Roles", String.join(",", customer.roles()));
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<CustomerResponse> extractCustomerFromHeader(String authHeader) {
        String token = extractToken(authHeader);

        return jwtDecoder.decode(token)
                .handle((decodedToken, sink) -> {
                    String customerId = decodedToken.getSubject();
                    String email = decodedToken.getClaimAsString("email");
                    String number = decodedToken.getClaimAsString("number");
                    String firstName = decodedToken.getClaimAsString("given_name");
                    String lastName = decodedToken.getClaimAsString("family_name");
                    List<String> roles = extractRoles(decodedToken);

                    if (customerId == null || email == null) {
                        sink.error(new CustomerNotFoundException("Could not extract customer details from the token"));
                        return;
                    }
                    sink.next(new CustomerResponse(customerId, firstName, lastName, email, number, roles));
                });
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authentication header");
        }
        return authHeader.substring(7);
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt decodedToken) {
        Map<String, Object> realmAccess = decodedToken.getClaimAsMap("realm_access");
        if (realmAccess == null) return List.of();

        Object roles = realmAccess.get("roles");
        return roles instanceof List<?> ? (List<String>) roles : List.of();
    }
}
