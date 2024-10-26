package pl.muybien.marketsync.finance.crypto.xrp;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.finance.FinanceComparator;
import pl.muybien.marketsync.finance.crypto.CryptoProvider;
import pl.muybien.marketsync.finance.FinanceService;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.subscription.SubscriptionListManager;

import java.math.BigDecimal;

@Service("xrp")
@Transactional
@RequiredArgsConstructor
public class XrpService implements FinanceService {

    private final CryptoProvider cryptoProvider;
    private final FinanceComparator financeComparator;
    private final SubscriptionListManager subscriptionListManager;
    private final XrpRepository repository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentFinance() {
        var cryptoPrice = cryptoProvider.fetchFinance("xrp").getPriceUsd();
        var subscriptions = repository.findAll();
        subscriptions.forEach(subscription -> {
            if (financeComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                repository.delete(subscription);
            }
        });
    }

    @Override
    @Transactional
    public void createAndSaveSubscription(Customer customer, String financeName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var crypto = Xrp.builder()
                .customer(customer)
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
