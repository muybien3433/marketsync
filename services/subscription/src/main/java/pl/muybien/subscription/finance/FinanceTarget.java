package pl.muybien.subscription.finance;

import java.math.BigDecimal;

public interface FinanceTarget {

    String getFinanceName();
    Long getId();
    Long getCustomerId();
    BigDecimal getUpperBoundPrice();
    BigDecimal getLowerBoundPrice();
}
