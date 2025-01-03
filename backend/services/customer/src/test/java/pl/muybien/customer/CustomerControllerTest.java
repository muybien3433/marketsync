package pl.muybien.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @InjectMocks
    private CustomerController controller;

    @Mock
    private CustomerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchCustomerFromHeader_validHeader() {
        String authHeader = "Bearer validToken";
        var expectedResponse = new CustomerResponse(
                "id", "John", "Doe", "john.doe@example.com");

        when(service.fetchCustomerFromHeader(authHeader)).thenReturn(expectedResponse);

        var response = controller.fetchCustomerFromHeader(authHeader);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(service, times(1)).fetchCustomerFromHeader(authHeader);
    }

    @Test
    void fetchCustomerFromHeader_invalidHeader() {
        String authHeader = "Berr invalidToken";

        when(service.fetchCustomerFromHeader(authHeader))
                .thenThrow(new IllegalArgumentException("Invalid Authorization header"));

        var ex = assertThrows(IllegalArgumentException.class, () -> controller.fetchCustomerFromHeader(authHeader));

        assertEquals("Invalid Authorization header", ex.getMessage());
        verify(service, times(1)).fetchCustomerFromHeader(authHeader);

    }
}