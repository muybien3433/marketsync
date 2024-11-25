package pl.muybien.subscription.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscription.kafka.SubscriptionNotification;
import pl.muybien.subscription.kafka.SubscriptionProducer;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceComparator {

    private final SubscriptionProducer subscriptionProducer;

    @Transactional
    public <T extends FinanceTarget> boolean priceMetSubscriptionCondition(BigDecimal priceUsd, T subscription) {
        if (subscription != null) {
            BigDecimal upperTargetPrice = subscription.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscription.getLowerBoundPrice();
            boolean currentValueEqualsOrGreaterThanTarget = priceUsd.compareTo(upperTargetPrice) >= 0;
            boolean currentValueEqualsOrLowerThanTarget = priceUsd.compareTo(lowerTargetPrice) <= 0;

            if (currentValueEqualsOrGreaterThanTarget) {
                subscriptionProducer.sendSubscriptionNotification(
                        SubscriptionNotification.builder()
                                .email(subscription.getCustomerEmail())
                                .subject("Your %s subscription notification!".formatted(subscription.getFinanceName()))
                                .body("Current %s value reached bound at: %s, your bound was %s"
                                        .formatted(subscription.getFinanceName(), priceUsd, upperTargetPrice))
                                .build()
                );
                return true;
            } else if (currentValueEqualsOrLowerThanTarget) {
                subscriptionProducer.sendSubscriptionNotification(
                        SubscriptionNotification.builder()
                                .email(subscription.getCustomerEmail())
                                .subject("Your %s subscription notification!".formatted(subscription.getFinanceName()))
                                .body("Current %s value reached bound at: %s, your bound was %s"
                                        .formatted(subscription.getFinanceName(), priceUsd, lowerTargetPrice))
                                .build()
                );
                return true;
            }
        }
        return false;
    }
}
