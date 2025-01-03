package pl.muybien.customer.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import pl.muybien.customer.exception.CustomerNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private JwtDecoder jwtDecoder;

    private CustomerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CustomerService(jwtDecoder);
    }

    @Test
    void fetchCustomerFromHeader_validHeader() {
        String authHeader = "Bearer validToken";
        var mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("validToken")).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn("12345");
        when(mockJwt.getClaimAsString("email")).thenReturn("john.doe@example.com");
        when(mockJwt.getClaimAsString("given_name")).thenReturn("John");
        when(mockJwt.getClaimAsString("family_name")).thenReturn("Doe");

        var response = service.fetchCustomerFromHeader(authHeader);

        assertNotNull(response);
        assertEquals("12345", response.id());
        assertEquals("john.doe@example.com", response.email());
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());

        verify(jwtDecoder, times(1)).decode("validToken");
    }

    @Test
    void fetchCustomerFromHeader_invalidHeader() {
        String authHeader = "invalidHeader";

        var ex = assertThrows(IllegalArgumentException.class, () -> service.fetchCustomerFromHeader(authHeader));

        assertEquals("Invalid Authorization header", ex.getMessage());
    }

    @Test
    void testDecodeTokenAndMapToCustomerResponse_customerNotFound() {
        String authHeader = "Bearer validToken";
        var mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("validToken")).thenReturn(mockJwt);
        when(mockJwt.getSubject()).thenReturn(null);
        when(mockJwt.getClaimAsString("email")).thenReturn(null);

        var ex = assertThrows(CustomerNotFoundException.class, () ->
                service.decodeTokenAndMapToCustomerResponse(authHeader));

        assertEquals("Customer not found", ex.getMessage());
        verify(jwtDecoder, times(1)).decode("validToken");
    }}