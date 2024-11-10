package pl.muybien.subscriptionservice.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceComparator {

    @Transactional
    public <T extends FinanceTarget> boolean currentPriceMetSubscriptionCondition(BigDecimal currentPriceUsd, T subscription) {
        if (subscription != null) {
            BigDecimal upperTargetPrice = subscription.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscription.getLowerBoundPrice();
            boolean currentValueEqualsOrGreaterThanTarget = currentPriceUsd.compareTo(upperTargetPrice) >= 0;
            boolean currentValueEqualsOrLowerThanTarget = currentPriceUsd.compareTo(lowerTargetPrice) <= 0;

            if (currentValueEqualsOrGreaterThanTarget) {
                sendNotification(subscription, currentPriceUsd, upperTargetPrice);
                return true;
            } else if (currentValueEqualsOrLowerThanTarget) {
                sendNotification(subscription, currentPriceUsd, lowerTargetPrice);
                return true;
            }
        }
        return false;
    }

    // TODO: Move this functionality to new microservice after kafka set
    private <T extends FinanceTarget> void sendNotification(T subscription,
                                                            BigDecimal currentPriceUsd,
                                                            BigDecimal targetPrice) {
        String email = subscription.getCustomerEmail();
        String subject = "Your %s subscription notification!".formatted(subscription.getName());
        String message = "Current %s value reached bound at: %s, your bound was %s".formatted(
                subscription.getName(), currentPriceUsd, targetPrice);

//        notificationService.sendNotification(email, subject, message);

    }
}
