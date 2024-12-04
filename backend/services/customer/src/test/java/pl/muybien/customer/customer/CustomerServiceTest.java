package pl.muybien.customer.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.muybien.customer.exception.CustomerNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService service;

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_shouldSaveCustomerAndReturnId() {
        var request = new CustomerRequest(1,"John", "Doe", "john.doe@example.com");
        var fixedDateTime = LocalDateTime.of(2024, 12, 1, 10, 30);

        when(repository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            customer.setId(1L);
            customer.setCreatedDate(fixedDateTime);
            return customer;
        });

        Long result = service.createCustomer(request);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(repository).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();

        assertThat(capturedCustomer.getFirstName()).isEqualTo("John");
        assertThat(capturedCustomer.getLastName()).isEqualTo("Doe");
        assertThat(capturedCustomer.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(capturedCustomer.getCreatedDate()).isEqualTo(fixedDateTime);
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void updateCustomer_shouldUpdateAndSaveCustomer() {
        var request = new CustomerRequest(1, "Jane", "Smith", "jane.smith@example.com");
        var existingCustomer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(existingCustomer));

        service.updateCustomer(request);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(repository).save(customerCaptor.capture());
        Customer capturedCustomer = customerCaptor.getValue();

        assertThat(capturedCustomer.getId()).isEqualTo(1L);
        assertThat(capturedCustomer.getFirstName()).isEqualTo("Jane");
        assertThat(capturedCustomer.getLastName()).isEqualTo("Smith");
        assertThat(capturedCustomer.getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void updateCustomer_shouldThrowExceptionIfCustomerNotFound() {
        var request = new CustomerRequest(1, "Jane", "Smith", "jane.smith@example.com");
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCustomer(request))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id 1 not found");

        verify(repository, never()).save(any());
    }

    @Test
    void findById_shouldReturnCustomerResponseWhenCustomerExists() {
        var customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        var customerResponse = new CustomerResponse(1L, "John", "Doe", "john.doe@example.com");

        when(repository.findById(1L)).thenReturn(Optional.of(customer));
        when(mapper.toCustomerResponse(customer)).thenReturn(customerResponse);

        CustomerResponse result = service.findById(1L);

        verify(repository).findById(1L);
        verify(mapper).toCustomerResponse(customer);
        assertThat(result).isEqualTo(customerResponse);
    }

    @Test
    void findById_shouldThrowExceptionWhenCustomerNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id 1 not found");

        verify(repository).findById(1L);
        verifyNoInteractions(mapper);
    }

    @Test
    void deleteCustomer_shouldDeleteCustomerById() {
        Long customerId = 1L;

        service.deleteCustomer(customerId);

        verify(repository, times(1)).deleteById(customerId);
    }
}