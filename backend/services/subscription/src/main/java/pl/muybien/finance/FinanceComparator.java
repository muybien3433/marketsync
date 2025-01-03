package pl.muybien.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.kafka.SubscriptionEmailConfirmation;
import pl.muybien.kafka.SubscriptionProducer;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceComparator {

    private final SubscriptionProducer subscriptionProducer;

    @Transactional
    public <T extends FinanceTarget> boolean priceMetSubscriptionCondition(BigDecimal price, T subscription) {
        if (subscription != null) {
            BigDecimal upperTargetPrice = subscription.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscription.getLowerBoundPrice();

            boolean currentValueEqualsOrGreaterThanTarget = false;
            if (upperTargetPrice != null) {
                currentValueEqualsOrGreaterThanTarget = price.compareTo(upperTargetPrice) >= 0;
            }

            boolean currentValueEqualsOrLowerThanTarget = false;
            if (lowerTargetPrice != null) {
                currentValueEqualsOrLowerThanTarget = price.compareTo(lowerTargetPrice) <= 0;
            }

            if (currentValueEqualsOrGreaterThanTarget) {
                subscriptionProducer.sendSubscriptionEmailNotification(
                        SubscriptionEmailConfirmation.builder()
                                .email(subscription.getCustomerEmail())
                                .subject("Your %s subscription notification!".formatted(subscription.getFinanceName()))
                                .body("Current %s value reached bound at: %s, your bound was %s"
                                        .formatted(subscription.getFinanceName(), price, upperTargetPrice))
                                .build()
                );
                return true;
            } else if (currentValueEqualsOrLowerThanTarget) {
                subscriptionProducer.sendSubscriptionEmailNotification(
                        SubscriptionEmailConfirmation.builder()
                                .email(subscription.getCustomerEmail())
                                .subject("Your %s subscription notification!".formatted(subscription.getFinanceName()))
                                .body("Current %s value reached bound at: %s, your bound was %s"
                                        .formatted(subscription.getFinanceName(), price, lowerTargetPrice))
                                .build()
                );
                return true;
            }
        }
        return false;
    }
}
