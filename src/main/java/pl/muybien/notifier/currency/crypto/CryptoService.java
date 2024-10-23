package pl.muybien.notifier.currency.crypto;

import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

public interface CryptoService {

    void fetchCurrentStock();
    void createAndSaveSubscription(Customer customer, String cryptoName,
                                                   BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd);

    void removeSubscription(CryptoTarget cryptoTarget);
}
