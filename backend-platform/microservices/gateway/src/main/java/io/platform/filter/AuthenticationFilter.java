package io.platform.filter;

import io.platform.exception.CustomerNotFoundException;
import io.platform.security.InternalTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final String loginPath = "/api/v1/users/login";
    private final String registerPath = "/api/v1/users/register";
    private final String walletWebsocketPath = "/api/ws-wallet/";

    private final JwtDecoder jwtDecoder;
    private final InternalTokenService internalTokenService;

    @Value("${internal-jwt.audience}")
    private String audience;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean isWebsocket = path.startsWith(walletWebsocketPath);
        boolean isPublicPath = path.equals(loginPath) || path.equals(registerPath) || isWebsocket;

        String resolvedAudience = resolveAudience(request);

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isWebsocket && (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer "))) {
            String tokenParam = UriComponentsBuilder.fromUriString(request.getRequestURL().toString())
                    .query(request.getQueryString())
                    .build()
                    .getQueryParams()
                    .getFirst("token");

            if (StringUtils.hasText(tokenParam)) {
                authHeader = "Bearer " + tokenParam;
            }
        }

        if (isPublicPath) {
            String internalJwt = internalTokenService.mint(resolvedAudience, null);

            MutableHttpServletRequest mutated = new MutableHttpServletRequest(request);
            removeCustomerHeaders(mutated);
            mutated.putHeader(HttpHeaders.AUTHORIZATION, "Bearer " + internalJwt);

            filterChain.doFilter(mutated, response);
            return;
        }

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is invalid");
            return;
        }

        String externalAuthorization = authHeader;

        CustomerResponse customer = extractCustomerFromHeader(authHeader);

        String internalJwt = internalTokenService.mint(resolvedAudience, customer);

        MutableHttpServletRequest mutated = new MutableHttpServletRequest(request);
        removeCustomerHeaders(mutated);

        mutated.putHeader(HttpHeaders.AUTHORIZATION, "Bearer " + internalJwt);
        mutated.putHeader("X-External-Authorization", externalAuthorization);
        mutated.putHeader("X-Customer-Id", customer.id());
        mutated.putHeader("X-Customer-Email", customer.email());
        mutated.putHeader("X-Customer-Number", customer.number());
        mutated.putHeader("X-Customer-FirstName", customer.firstName());
        mutated.putHeader("X-Customer-LastName", customer.lastName());
        mutated.putHeader("X-Customer-Roles", String.join(",", customer.roles()));

        filterChain.doFilter(mutated, response);
    }

    private String resolveAudience(HttpServletRequest request) {
        return audience;
    }

    private CustomerResponse extractCustomerFromHeader(String authHeader) {
        String token = extractToken(authHeader);

        Jwt decodedToken = jwtDecoder.decode(token);

        String customerId = decodedToken.getSubject();
        String email = decodedToken.getClaimAsString("email");
        String number = decodedToken.getClaimAsString("number");
        String firstName = decodedToken.getClaimAsString("given_name");
        String lastName = decodedToken.getClaimAsString("family_name");
        List<String> roles = extractRoles(decodedToken);

        if (customerId == null || email == null) {
            throw new CustomerNotFoundException("Could not extract customer details from the token");
        }

        return new CustomerResponse(customerId, firstName, lastName, email, number, roles);
    }

    private String extractToken(String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authentication header");
        }
        return authHeader.substring(7);
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRoles(Jwt decodedToken) {
        Map<String, Object> realmAccess = decodedToken.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            return List.of();
        }

        Object roles = realmAccess.get("roles");
        return roles instanceof List<?> ? (List<String>) roles : List.of();
    }

    private void removeCustomerHeaders(MutableHttpServletRequest request) {
        request.removeHeader("X-Customer-Id");
        request.removeHeader("X-Customer-Email");
        request.removeHeader("X-Customer-Number");
        request.removeHeader("X-Customer-FirstName");
        request.removeHeader("X-Customer-LastName");
        request.removeHeader("X-Customer-Roles");
        request.removeHeader("X-External-Authorization");
    }
}
