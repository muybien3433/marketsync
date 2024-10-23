package pl.muybien.notifier.currency.crypto.tether;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.notifier.currency.crypto.CryptoCurrencyComparator;
import pl.muybien.notifier.currency.crypto.CryptoCurrencyProvider;
import pl.muybien.notifier.currency.crypto.CryptoService;
import pl.muybien.notifier.currency.crypto.CryptoTarget;
import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

@Service("tether")
@Transactional
@RequiredArgsConstructor
public class TetherService implements CryptoService {

    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CryptoCurrencyComparator cryptoCurrencyComparator;
    private final TetherRepository tetherRepository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentStock() {
        var cryptoPrice = cryptoCurrencyProvider.fetchCurrencyByUri("tether").getPriceUsd();
        var subscriptions = tetherRepository.findAll();
        subscriptions.forEach(subscription -> {
            if (cryptoCurrencyComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                removeSubscription(subscription);
            }
        });
    }

    @Override
    public void createAndSaveSubscription(Customer customer, String cryptoName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var tether = Tether.builder()
                .customer(customer)
                .name(cryptoName)
                .upperBoundPrice(upperPriceInUsd)
                .lowerBoundPrice(lowerPriceInUsd)
                .build();
        tetherRepository.save(tether);
    }

    @Override
    public void removeSubscription(CryptoTarget cryptoTarget) {
        tetherRepository.deleteById(cryptoTarget.getId());
    }
}
