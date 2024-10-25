package pl.muybien.marketsync.currency.crypto.binancecoin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.currency.CurrencyComparator;
import pl.muybien.marketsync.currency.crypto.CryptoCurrencyProvider;
import pl.muybien.marketsync.currency.CurrencyService;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.subscription.SubscriptionListManager;

import java.math.BigDecimal;

@Service("binance-coin")
@Transactional
@RequiredArgsConstructor
public class BinanceService implements CurrencyService {

    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CurrencyComparator currencyComparator;
    private final SubscriptionListManager subscriptionListManager;
    private final BinanceRepository repository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentStock() {
        var cryptoPrice = cryptoCurrencyProvider.fetchCurrency("binance-coin").getPriceUsd();
        var subscriptions = repository.findAll();
        subscriptions.forEach(subscription -> {
            if (currencyComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                repository.delete(subscription);
            }
        });
    }

    @Override
    @Transactional
    public void createAndSaveSubscription(Customer customer, String cryptoName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var crypto = Binance.builder()
                .customer(customer)
                .name(cryptoName)
                .upperBoundPrice(upperPriceInUsd)
                .lowerBoundPrice(lowerPriceInUsd)
                .build();
        repository.save(crypto);
        subscriptionListManager.addSubscriptionToList(crypto);
    }

    @Override
    @Transactional
    public void removeSubscription(OidcUser oidcUser, Long id) {
        repository.findById(id).ifPresentOrElse(crypto -> {
            if (crypto.getCustomer().getEmail().equals(oidcUser.getEmail())) {
                repository.delete(crypto);
                subscriptionListManager.removeSubscriptionFromList(crypto);
            } else {
                throw new AccessDeniedException("You are not authorized to delete this subscription.");
            }
        }, () -> {
            throw new EntityNotFoundException("Subscription with id %d not found.".formatted(id));
        });
    }
}
