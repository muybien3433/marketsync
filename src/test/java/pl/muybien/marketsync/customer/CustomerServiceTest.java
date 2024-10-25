package pl.muybien.marketsync.customer;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findOrCreateCustomerExists() {
        String email = "existing@email.com";
        String name = "existingName";
        var existingCustomer = Customer.builder()
                .email(email)
                .name(name)
                .build();

        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(existingCustomer));

        customerService.findOrCreateCustomer(email, name);

        verify(customerRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void findOrCreateCustomerNotExists() {
        String email = "notExisting@email.com";
        String name = "notExistingName";

        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        customerService.findOrCreateCustomer(email, name);

        verify(customerRepository, times(1)).findByEmail(email);
        verify(customerRepository, times(1)).save(argThat(customer ->
                customer.getEmail().equals(email) && customer.getName().equals(name)));

    }

    @Test
    void findCustomerByEmailSuccess() {
        String email = "test@email.com";
        var mockCustomer = mock(Customer.class);

        when(customerRepository.findByEmail(email)).thenReturn(Optional.ofNullable(mockCustomer));

        Customer customer = customerService.findCustomerByEmail(email);

        assertNotNull(customer);
        assertEquals(customer, mockCustomer);
        verify(customerRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void findCustomerByEmailNotFound() {
        String email = "test@email.com";
        when(customerRepository.findByEmail(email)).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                customerService.findCustomerByEmail(email));

        assertEquals("Customer with email test@email.com not found.", e.getMessage());
        verify(customerRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(customerRepository);
    }
}