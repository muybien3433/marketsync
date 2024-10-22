package pl.muybien.notifier.currency;

import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

public interface CurrencyService {
    void createAndSaveSubscription(Customer customer,
                                   String cryptoName,
                                   BigDecimal upperPriceInUsd,
                                   BigDecimal lowerPriceInUsd);
}
