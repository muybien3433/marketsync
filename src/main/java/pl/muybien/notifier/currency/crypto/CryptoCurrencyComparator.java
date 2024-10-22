package pl.muybien.notifier.currency.crypto;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CryptoCurrencyComparator {

    @Transactional
    public void updateValueAndCompareWithSubscribersGoals(Crypto currentCrypto, List<CryptoTarget> subscribers) {
        BigDecimal currentPriceUsd = currentCrypto.getPriceUsd();

        subscribers.forEach(subscriber -> compareCurrentValueWithTarget(currentPriceUsd, subscriber));
    }

    private <T extends CryptoTarget> void compareCurrentValueWithTarget(BigDecimal currentPriceUsd, T cryptoTarget) {
        if (cryptoTarget != null) {
            System.out.println("Subscriber is not null"); // TODO: delete
            System.out.println("Current value usd: " + currentPriceUsd);
            BigDecimal upperTargetPrice = cryptoTarget.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = cryptoTarget.getLowerBoundPrice();
            boolean currentValueEqualsOrGraterThanTarget = currentPriceUsd.compareTo(upperTargetPrice) >= 0;
            boolean currentValueEqualsOrLowerThanTarget = currentPriceUsd.compareTo(lowerTargetPrice) <= 0;

            if (currentValueEqualsOrGraterThanTarget) {
                // TODO: Send notification via email instead
                System.out.println("Current value: " + currentPriceUsd + " is >= than target: " + upperTargetPrice);
            } else if (currentValueEqualsOrLowerThanTarget) {
                // TODO: Send notification via email instead
                System.out.println("Current value: " + currentPriceUsd + " is <= target: " + lowerTargetPrice);
            }
        } else {
            System.out.println("Subscriber is null"); // TODO: create SubscriberNotFoundException
        }
    }
}
