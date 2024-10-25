package pl.muybien.marketsync.asset;

import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface AssetTarget {

    String getName();
    Long getId();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
    Customer getCustomer();
}
