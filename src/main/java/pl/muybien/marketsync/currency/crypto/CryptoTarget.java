package pl.muybien.marketsync.currency.crypto;

import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface CryptoTarget {

    String getName();
    Long getId();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
    Customer getCustomer();
}
