package pl.muybien.notifier.currency.crypto.bitcoin;

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

@Service("bitcoin")
@Transactional
@RequiredArgsConstructor
public class BitcoinService implements CryptoService {

    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CryptoCurrencyComparator cryptoCurrencyComparator;
    private final BitcoinRepository bitcoinRepository;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentStock() {
        var cryptoPrice = cryptoCurrencyProvider.fetchCurrencyByUri("bitcoin").getPriceUsd();
        var subscriptions = bitcoinRepository.findAll();
        subscriptions.forEach(subscription -> {
            if (cryptoCurrencyComparator.currentPriceMetSubscriptionCondition(cryptoPrice, subscription)) {
                removeSubscription(subscription);
            }
        });
    }

    @Override
    public void createAndSaveSubscription(Customer customer, String cryptoName,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var bitcoin = Bitcoin.builder()
                .customer(customer)
                .name(cryptoName)
                .upperBoundPrice(upperPriceInUsd)
                .lowerBoundPrice(lowerPriceInUsd)
                .build();
        bitcoinRepository.save(bitcoin);
    }

    @Override
    public void removeSubscription(CryptoTarget cryptoTarget) {
        bitcoinRepository.deleteById(cryptoTarget.getId());
    }
}
