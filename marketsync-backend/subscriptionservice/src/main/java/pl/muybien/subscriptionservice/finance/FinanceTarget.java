package pl.muybien.subscriptionservice.finance;

import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface FinanceTarget {

    String getName();
    Long getId();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
    Customer getCustomer();
}
