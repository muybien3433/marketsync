package pl.muybien.subscriptionservice.finance.crypto.solana;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscriptionservice.finance.FinanceComparator;
import pl.muybien.subscriptionservice.finance.FinanceService;
import pl.muybien.subscriptionservice.finance.crypto.CryptoProvider;
import pl.muybien.subscriptionservice.subscription.SubscriptionListManager;

import java.math.BigDecimal;

@Service("solana")
@Transactional
@RequiredArgsConstructor
public class SolanaService implements FinanceService {

    private final CryptoProvider cryptoProvider;
    private final FinanceComparator financeComparator;
    private final SubscriptionListManager subscriptionListManager;
    private final SolanaReposiroty repository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentFinance() {
        var cryptoPrice = cryptoProvider.fetchFinance("solana").getPriceUsd();
        var subscriptions = repository.findAll();
        subscriptions.forEach(subscription -> {
            if (financeComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                repository.delete(subscription);
            }
        });
    }

    @Override
    @Transactional
    public void createAndSaveSubscription(String customerEmail, String financeName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var crypto = Solana.builder()
                .customerEmail(customerEmail)
                .name(financeName)
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
            if (crypto.getCustomerEmail().equals(oidcUser.getEmail())) {
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
