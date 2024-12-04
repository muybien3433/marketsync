package pl.muybien.subscription.finance;

import pl.muybien.subscription.subscription.SubscriptionDetail;

import java.math.BigDecimal;

public interface FinanceService {

    void fetchCurrentFinanceAndCompare();
    SubscriptionDetail createIncreaseSubscription(BigDecimal value, String customerEmail, Long customerId);
    SubscriptionDetail createDecreaseSubscription(BigDecimal value, String customerEmail, Long customerId);
    void deleteSubscription(Long subscriptionId, Long customerId);
}
