package pl.muybien.marketsync.currency;

import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface CurrencyTarget {

    String getName();
    Long getId();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
    Customer getCustomer();
}
