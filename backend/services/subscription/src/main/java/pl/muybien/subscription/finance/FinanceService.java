package pl.muybien.subscription.finance;

import pl.muybien.subscription.subscription.SubscriptionDetail;

import java.math.BigDecimal;

public interface FinanceService {

    void fetchCurrentFinanceAndCompare();
    SubscriptionDetail createIncreaseSubscription(BigDecimal value, String customerEmail, String customerId);
    SubscriptionDetail createDecreaseSubscription(BigDecimal value, String customerEmail, String customerId);
    void deleteSubscription(Long subscriptionId, String customerId);
}
