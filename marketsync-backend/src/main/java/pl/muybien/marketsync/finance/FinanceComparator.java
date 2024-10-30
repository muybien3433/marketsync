package pl.muybien.marketsync.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.notification.NotificationService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceComparator {

    private final NotificationService notificationService;

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

    private <T extends FinanceTarget> void sendNotification(T subscription,
                                                            BigDecimal currentPriceUsd,
                                                            BigDecimal targetPrice) {
        String email = subscription.getCustomer().getEmail();
        String subject = "Your %s subscription notification!".formatted(subscription.getName());
        String message = "Current %s value reached bound at: %s, your bound was %s".formatted(
                subscription.getName(), currentPriceUsd, targetPrice);

        notificationService.sendNotification(email, subject, message);

    }
}
