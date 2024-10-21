package pl.muybien.notifier.currency;

import pl.muybien.notifier.currency.crypto.CryptoTarget;
import pl.muybien.notifier.customer.Customer;

public interface CurrencyService {
    void createAndSaveSubscription(Customer customer, CryptoTarget cryptoTarget);
}
