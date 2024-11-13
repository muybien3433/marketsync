package pl.muybien.customerservice.customer;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    // TODO: transactional must be with jpa and kafka transactions to ensure customer and wallet were created
    public void createCustomerIfNotPresent(String email, String name) {
        boolean customerIsNotPresent = customerRepository.findByEmail(email).isEmpty();

        if (customerIsNotPresent) {
            var customer = Customer.builder()
                    .email(email)
                    .name(name)
                    .build();
            customerRepository.save(customer);
            // TODO: message to WalletService to createNewWallet(email);
        }
    }

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer with email %s not found.".formatted(email)));
    }
}
