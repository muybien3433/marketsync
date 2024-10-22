package pl.muybien.notifier.currency.crypto;

import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

public interface CryptoTarget {

    String getName();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
    Customer getCustomer();
}
