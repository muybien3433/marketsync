package pl.muybien.notifier.currency.crypto.ethereum;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.notifier.currency.crypto.CryptoCurrencyComparator;
import pl.muybien.notifier.currency.crypto.CryptoCurrencyProvider;
import pl.muybien.notifier.currency.crypto.CryptoService;
import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

@Service("ethereum")
@Transactional
@RequiredArgsConstructor
public class EthereumService implements CryptoService {

    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CryptoCurrencyComparator cryptoCurrencyComparator;
    private final EthereumRepository ethereumRepository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentStock() {
        var cryptoPrice = cryptoCurrencyProvider.fetchCurrencyByUri("ethereum").getPriceUsd();
        var subscriptions = ethereumRepository.findAll();
        subscriptions.forEach(subscription -> {
            if (cryptoCurrencyComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                ethereumRepository.delete(subscription);
            }
        });
    }

    @Override
    public void createAndSaveSubscription(Customer customer, String cryptoName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var ethereum = Ethereum.builder()
                .customer(customer)
                .name(cryptoName)
                .upperBoundPrice(upperPriceInUsd)
                .lowerBoundPrice(lowerPriceInUsd)
                .build();
        ethereumRepository.save(ethereum);
    }

    @Override
    @Transactional
    public void removeSubscription(OidcUser oidcUser, Long id) {
        ethereumRepository.findById(id).ifPresentOrElse(ethereum -> {
            if (ethereum.getCustomer().getEmail().equals(oidcUser.getEmail())) {
                ethereumRepository.delete(ethereum);
            } else {
                throw new AccessDeniedException("You are not authorized to delete this subscription.");
            }
        }, () -> {
            throw new EntityNotFoundException("Subscription with id %d not found.".formatted(id));
        });
    }
}
