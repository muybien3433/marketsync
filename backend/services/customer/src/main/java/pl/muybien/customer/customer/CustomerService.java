package pl.muybien.customer.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import pl.muybien.customer.exception.CustomerNotFoundException;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final JwtDecoder jwtDecoder;

    CustomerResponse fetchCustomerFromHeader(String authHeader) {
        return decodeTokenAndMapToCustomerResponse(authHeader);
    }

    private CustomerResponse decodeTokenAndMapToCustomerResponse(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        var decodedToken = jwtDecoder.decode(token);

        String customerId = decodedToken.getSubject();
        String email = decodedToken.getClaimAsString("email");
        String firstName = decodedToken.getClaimAsString("given_name");
        String lastName = decodedToken.getClaimAsString("family_name");

        if (customerId == null || email == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        return new CustomerResponse(customerId, email, firstName, lastName);
    }

    private String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        return authHeader.substring(7);
    }
}
