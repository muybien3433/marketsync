package pl.muybien.subscription.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscription.kafka.SubscriptionConfirmation;
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

            boolean currentValueEqualsOrGreaterThanTarget = false;
            if (upperTargetPrice != null) {
                currentValueEqualsOrGreaterThanTarget = priceUsd.compareTo(upperTargetPrice) >= 0;
            }

            boolean currentValueEqualsOrLowerThanTarget = false;
            if (lowerTargetPrice != null) {
                currentValueEqualsOrLowerThanTarget = priceUsd.compareTo(lowerTargetPrice) <= 0;
            }

            if (currentValueEqualsOrGreaterThanTarget) {
                subscriptionProducer.sendSubscriptionNotification(
                        SubscriptionConfirmation.builder()
                                .email(subscription.getCustomerEmail())
                                .subject("Your %s subscription notification!".formatted(subscription.getFinanceName()))
                                .body("Current %s value reached bound at: %s, your bound was %s"
                                        .formatted(subscription.getFinanceName(), priceUsd, upperTargetPrice))
                                .build()
                );
                return true;
            } else if (currentValueEqualsOrLowerThanTarget) {
                subscriptionProducer.sendSubscriptionNotification(
                        SubscriptionConfirmation.builder()
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
