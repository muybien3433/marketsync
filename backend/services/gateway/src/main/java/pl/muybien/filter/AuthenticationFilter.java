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
                            HttpHeaders newHeaders = new HttpHeaders();
                            newHeaders.addAll(request.getHeaders());
                            newHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);
                            newHeaders.add("X-Customer-Id", customer.id());
                            newHeaders.add("X-Customer-Email", customer.email());
                            newHeaders.add("X-Customer-FirstName", customer.firstName());
                            newHeaders.add("X-Customer-LastName", customer.lastName());
                            newHeaders.add("X-Customer-Roles", String.join(",", customer.roles()));

                            ServerHttpRequest modifiedRequest = new ServerHttpRequestDecorator(request) {
                                @Override
                                @NonNull
                                public HttpHeaders getHeaders() {
                                    return HttpHeaders.readOnlyHttpHeaders(newHeaders);
                                }
                            };
                            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                        }
                );
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
                    String firstName = decodedToken.getClaimAsString("given_name");
                    String lastName = decodedToken.getClaimAsString("family_name");
                    List<String> roles = extractRoles(decodedToken);

                    if (customerId == null || email == null) {
                        sink.error(new CustomerNotFoundException("Customer not found"));
                        return;
                    }
                    sink.next(new CustomerResponse(customerId, firstName, lastName, email, roles));
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
