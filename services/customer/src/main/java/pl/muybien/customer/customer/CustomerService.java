package pl.muybien.customer.customer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.customer.exception.CustomerNotFoundException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerMapper mapper;
    private final CustomerRepository repository;

    @Transactional
    public Long createCustomer(CustomerRequest request) {
        var customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .createdDate(LocalDateTime.now())
                .build();
        repository.save(customer);
        return customer.getId();
    }

    @Transactional
    public void updateCustomer(CustomerRequest request) {
        var customer = repository.findById(request.id())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer with id %d not found".formatted(request.id())));
        mergeCustomer(customer, request);
        repository.save(customer);
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstName())) {
            customer.setFirstName(request.firstName());
        }
        if (StringUtils.isNotBlank(request.lastName())) {
            customer.setLastName(request.lastName());
        }
        if (StringUtils.isNotBlank(request.email())) {
            customer.setEmail(request.email());
        }
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toCustomerResponse)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer with id %d not found".formatted(id)));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }
}
