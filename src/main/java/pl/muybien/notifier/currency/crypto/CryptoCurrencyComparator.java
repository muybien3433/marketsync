package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CryptoCurrencyComparator {

    @Transactional
    public  <T extends CryptoTarget> void compareValueWithSubscriptionAndNotifyIfNecessary(BigDecimal currentPriceUsd, T subscription) {
        if (subscription != null) {
            BigDecimal upperTargetPrice = subscription.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscription.getLowerBoundPrice();
            boolean currentValueEqualsOrGraterThanTarget = currentPriceUsd.compareTo(upperTargetPrice) >= 0;
            boolean currentValueEqualsOrLowerThanTarget = currentPriceUsd.compareTo(lowerTargetPrice) <= 0;

            if (currentValueEqualsOrGraterThanTarget) {
                // TODO: Send notification via email instead
                System.out.println("Current value: " + currentPriceUsd + " is >= than target: " + upperTargetPrice);

//                cryptoSubscriptionManager.removeSubscription(subscription, subscription.getName());
            } else if (currentValueEqualsOrLowerThanTarget) {
                // TODO: Send notification via email instead
                System.out.println("Current value: " + currentPriceUsd + " is <= target: " + lowerTargetPrice);
//                cryptoService.removeSubscription(subscription);
            }
        } else {
            System.out.println("Subscriber is null"); // TODO: create SubscriberNotFoundException
        }
    }

    public <T extends CryptoTarget> boolean currentPriceMetSubscriptionCondition(BigDecimal currentPriceUsd, T subscription) {
        System.out.println("Current value: " + currentPriceUsd);
        if (subscription != null) {
            BigDecimal upperTargetPrice = subscription.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscription.getLowerBoundPrice();
            boolean currentValueEqualsOrGraterThanTarget = currentPriceUsd.compareTo(upperTargetPrice) >= 0;
            boolean currentValueEqualsOrLowerThanTarget = currentPriceUsd.compareTo(lowerTargetPrice) <= 0;

            if (currentValueEqualsOrGraterThanTarget) {
                // TODO: Send notification via email instead
                System.out.println("Current value: " + currentPriceUsd + " is >= than target: " + upperTargetPrice);
                return true;
            } else if (currentValueEqualsOrLowerThanTarget) {
                // TODO: Send notification via email instead
                System.out.println("Current value: " + currentPriceUsd + " is <= target: " + lowerTargetPrice);
                return true;
            }
        }
        return false;
    }
}