package pl.muybien.marketsync.asset.crypto.steth;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.asset.AssetComparator;
import pl.muybien.marketsync.asset.crypto.CryptoProvider;
import pl.muybien.marketsync.asset.AssetService;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.subscription.SubscriptionListManager;

import java.math.BigDecimal;

@Service("steth")
@Transactional
@RequiredArgsConstructor
public class StethService implements AssetService {

    private final CryptoProvider cryptoProvider;
    private final AssetComparator assetComparator;
    private final SubscriptionListManager subscriptionListManager;
    private final StethRepository repository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentStock() {
        var cryptoPrice = cryptoProvider.fetchAsset("steth").getPriceUsd();
        var subscriptions = repository.findAll();
        subscriptions.forEach(subscription -> {
            if (assetComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                repository.delete(subscription);
            }
        });
    }

    @Override
    @Transactional
    public void createAndSaveSubscription(Customer customer, String assetName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var crypto = Steth.builder()
                .customer(customer)
                .name(assetName)
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
