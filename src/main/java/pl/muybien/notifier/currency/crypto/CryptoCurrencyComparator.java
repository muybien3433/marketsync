package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.notifier.notification.email.EmailService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CryptoCurrencyComparator {

    private final EmailService emailService;

    @Transactional
    public <T extends CryptoTarget> boolean currentPriceMetSubscriptionCondition(BigDecimal currentPriceUsd, T subscription) {
        if (subscription != null) {
            BigDecimal upperTargetPrice = subscription.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscription.getLowerBoundPrice().multiply(BigDecimal.valueOf(-1));
            boolean currentValueEqualsOrGraterThanTarget = currentPriceUsd.compareTo(upperTargetPrice) >= 0;
            boolean currentValueEqualsOrLowerThanTarget = currentPriceUsd.compareTo(lowerTargetPrice) <= 0;

            if (currentValueEqualsOrGraterThanTarget) {
                emailService.sendEmail(
                        subscription.getCustomer().getEmail(),
                        "Your %s subscription notify!".formatted(subscription.getName()),
                        ("Current %s value reached upper bound at: %s " +
                                "your bound was %s").formatted(
                                subscription.getName(), currentPriceUsd, subscription.getUpperBoundPrice()));
                return true;
            } else if (currentValueEqualsOrLowerThanTarget) {
                emailService.sendEmail(
                        subscription.getCustomer().getEmail(),
                        "Your %s subscription notify!".formatted(subscription.getName()),
                        ("Current %s value reached lower bound at: %s " +
                                "your bound was %s").formatted(
                                subscription.getName(), currentPriceUsd, subscription.getUpperBoundPrice()));
                return true;
            }
        }
        return false;
    }
}
