package pl.muybien.notifier.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public void findOrCreateCustomer(String email, String name) {
        customerRepository.findByEmail(email)
                .orElseGet(() -> {
                    var customer = Customer.builder()
                            .email(email)
                            .name(name)
                            .build();
                    return customerRepository.save(customer);
                });
    }

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    }
}
