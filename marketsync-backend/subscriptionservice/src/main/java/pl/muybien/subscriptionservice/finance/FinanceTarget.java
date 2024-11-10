package pl.muybien.subscriptionservice.finance;

import java.math.BigDecimal;

public interface FinanceTarget {

    String getName();
    Long getId();
    String getCustomerEmail();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
}
