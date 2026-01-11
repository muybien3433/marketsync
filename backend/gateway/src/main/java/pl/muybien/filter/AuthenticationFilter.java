package pl.muybien.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import pl.muybien.exception.CustomerNotFoundException;
import pl.muybien.security.InternalTokenService;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final String loginPath = "/api/v1/users/login";
    private final String registerPath = "/api/v1/users/register";
    private final String walletWebsocketPath = "/api/ws-wallet/";

    private final ReactiveJwtDecoder jwtDecoder;
    private final InternalTokenService internalTokenService;

    @Value("${internal-jwt.audience}")
    private String audience;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        boolean isWebsocket = path.startsWith(walletWebsocketPath);
        boolean isPublicPath = path.equals(loginPath) || path.equals(registerPath) || isWebsocket;

        String audience = resolveAudience(exchange);

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (isWebsocket && (authHeader == null || !authHeader.startsWith("Bearer "))) {
            String tokenParam = request.getQueryParams().getFirst("token");
            if (tokenParam != null && !tokenParam.isBlank()) {
                authHeader = "Bearer " + tokenParam;
            }
        }

        if (isPublicPath) {
            return internalTokenService.mint(audience, null)
                    .flatMap(internalJwt -> {
                        ServerHttpRequest mutatedRequest = request.mutate()
                                .headers(headers -> {
                                    headers.remove("X-Customer-Id");
                                    headers.remove("X-Customer-Email");
                                    headers.remove("X-Customer-Number");
                                    headers.remove("X-Customer-FirstName");
                                    headers.remove("X-Customer-LastName");
                                    headers.remove("X-Customer-Roles");
                                    headers.remove("X-External-Authorization");

                                    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + internalJwt);
                                })
                                .build();

                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new IllegalArgumentException("Authorization header is invalid"));
        }

        String externalAuthorization = authHeader;

        return extractCustomerFromHeader(authHeader)
                .flatMap(customer -> internalTokenService.mint(audience, customer)
                        .map(internalJwt -> new Bundle(customer, internalJwt)))
                .flatMap(bundle -> {
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .headers(headers -> {
                                headers.remove("X-Customer-Id");
                                headers.remove("X-Customer-Email");
                                headers.remove("X-Customer-Number");
                                headers.remove("X-Customer-FirstName");
                                headers.remove("X-Customer-LastName");
                                headers.remove("X-Customer-Roles");
                                headers.remove("X-External-Authorization");

                                headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bundle.internalJwt());
                                headers.set("X-External-Authorization", externalAuthorization);
                                headers.set("X-Customer-Id", bundle.customer().id());
                                headers.set("X-Customer-Email", bundle.customer().email());
                                headers.set("X-Customer-Number", bundle.customer().number());
                                headers.set("X-Customer-FirstName", bundle.customer().firstName());
                                headers.set("X-Customer-LastName", bundle.customer().lastName());
                                headers.set("X-Customer-Roles", String.join(",", bundle.customer().roles()));
                            })
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    private String resolveAudience(ServerWebExchange exchange) {
        //        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        //        if (route == null) return "unknown-service";
        //        return route.getId();

        return audience;
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

    private record Bundle(CustomerResponse customer, String internalJwt) {
    }
}
