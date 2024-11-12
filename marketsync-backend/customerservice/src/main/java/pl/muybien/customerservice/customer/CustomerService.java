package pl.muybien.customerservice.customer;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.wallet.Wallet;
import pl.muybien.marketsync.wallet.WalletService;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final WalletService walletService;

    @Transactional
    public void createCustomerIfNotPresent(String email, String name) {
        boolean customerIsNotPresent = customerRepository.findByEmail(email).isEmpty();

        if (customerIsNotPresent) {
            var customer = Customer.builder()
                    .email(email)
                    .name(name)
                    .build();
            customerRepository.save(customer);
            walletService.saveNewWallet(Wallet.builder().customer(customer).build());
        }
    }

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer with email %s not found.".formatted(email)));
    }
}
