package pl.muybien.marketsync.customer;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.wallet.Wallet;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public void createCustomerIfNotPresent(String email, String name) {
        customerRepository.findByEmail(email).ifPresentOrElse(
                _ -> {
                },
                () -> {
                    var customer = Customer.builder()
                            .email(email)
                            .name(name)
                            .wallet(new Wallet())
                            .build();
                    customerRepository.save(customer);
                }
        );
    }

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer with email %s not found.".formatted(email)));
    }
}
