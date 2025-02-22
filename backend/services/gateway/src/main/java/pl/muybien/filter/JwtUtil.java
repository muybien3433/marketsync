package pl.muybien.filter;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import pl.muybien.exception.CustomerNotFoundException;

import java.util.List;
import java.util.Map;

@Service
public class JwtUtil {

    private final JwtDecoder jwtDecoder;

    public JwtUtil(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public CustomerResponse extractCustomerFromHeader(String authHeader) {
        String token = extractToken(authHeader);
        Jwt decodedToken = jwtDecoder.decode(token);

        String customerId = decodedToken.getSubject();
        String email = decodedToken.getClaimAsString("email");
        String firstName = decodedToken.getClaimAsString("given_name");
        String lastName = decodedToken.getClaimAsString("family_name");
        List<String> roles = extractRoles(decodedToken);

        if (customerId == null || email == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        return new CustomerResponse(customerId, firstName, lastName, email, roles);
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
