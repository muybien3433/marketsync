package pl.muybien.marketsync.currency.crypto.dogecoin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.currency.crypto.CryptoCurrencyComparator;
import pl.muybien.marketsync.currency.crypto.CryptoCurrencyProvider;
import pl.muybien.marketsync.currency.crypto.CryptoService;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

@Service("dogecoin")
@Transactional
@RequiredArgsConstructor
public class DogecoinService implements CryptoService {

    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CryptoCurrencyComparator cryptoCurrencyComparator;
    private final DogecoinRepository repository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentStock() {
        var cryptoPrice = cryptoCurrencyProvider.fetchCurrencyByUri("dogecoin").getPriceUsd();
        var subscriptions = repository.findAll();
        subscriptions.forEach(subscription -> {
            if (cryptoCurrencyComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                repository.delete(subscription);
            }
        });
    }

    @Override
    @Transactional
    public void createAndSaveSubscription(Customer customer, String cryptoName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var crypto = Dogecoin.builder()
                .customer(customer)
                .name(cryptoName)
                .upperBoundPrice(upperPriceInUsd)
                .lowerBoundPrice(lowerPriceInUsd)
                .build();
        repository.save(crypto);
    }

    @Override
    @Transactional
    public void removeSubscription(OidcUser oidcUser, Long id) {
        repository.findById(id).ifPresentOrElse(crypto -> {
            if (crypto.getCustomer().getEmail().equals(oidcUser.getEmail())) {
                repository.delete(crypto);
            } else {
                throw new AccessDeniedException("You are not authorized to delete this subscription.");
            }
        }, () -> {
            throw new EntityNotFoundException("Subscription with id %d not found.".formatted(id));
        });
    }
}