package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.notifier.currency.CurrencyService;
import pl.muybien.notifier.customer.CustomerService;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CryptoService extends Crypto {

    private final WebClient webClient;
    private final CustomerService customerService;

    public Mono<Crypto> findCurrencyByUri(String uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(CryptoResponse.class)
                .map(CryptoMapper::mapToCrypto);
    }

    @Transactional
    public void addSubscription(Jwt jwt, String uri,
                                String upperValueInPercent, String lowerValueInPercent) {

        if (upperValueInPercent == null && lowerValueInPercent == null) {
            throw new IllegalArgumentException("At least upper or lower bound should be set."); // TODO: proper status and exception
        }

        var customer = customerService.createCustomerIfNotExists(jwt);

        var currentCrypto = (CurrencyService) findCurrencyByUri(uri).block();
        var targetCrypto = (CryptoTarget) currentCrypto;
        if (currentCrypto == null) {
            throw new RuntimeException("Could not fetch current crypto."); // TODO: status 500 exception
        }

        currentCrypto.createAndSaveSubscription(customer, targetCrypto);
    }

    @Transactional
    public void updateValueAndCompareWithSubscribersGoals(Crypto currentCrypto, List<CryptoTarget> subscribers) {
        BigDecimal currentPriceUsd = currentCrypto.getPriceUsd();

        subscribers.forEach(subscriber -> compareCurrentValueWithTarget(currentPriceUsd, subscriber));
    }

    private <T extends CryptoTarget> void compareCurrentValueWithTarget(BigDecimal currentPriceUsd, T subscriber) {
        if (subscriber != null) {
            System.out.println("Subscriber is not null"); // TODO: delete
            BigDecimal upperTargetPrice = subscriber.getUpperBoundPrice();
            BigDecimal lowerTargetPrice = subscriber.getLowerBoundPrice();
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
