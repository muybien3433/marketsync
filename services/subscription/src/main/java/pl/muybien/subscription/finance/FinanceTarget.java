package pl.muybien.subscription.finance;

import java.math.BigDecimal;

public interface FinanceTarget {

    Long getId();
    Long getCustomerId();
    String getCustomerEmail();
    String getFinanceName();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
}
