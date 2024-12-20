package pl.muybien.subscription.finance;

import java.math.BigDecimal;

public interface FinanceTarget {

    Long getId();
    String getCustomerId();
    String getCustomerEmail();
    String getFinanceName();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
}
