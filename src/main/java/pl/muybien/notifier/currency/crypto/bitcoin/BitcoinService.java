package pl.muybien.notifier.currency.crypto.bitcoin;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.notifier.currency.CurrencyService;
import pl.muybien.notifier.currency.crypto.CryptoCurrencyComparator;
import pl.muybien.notifier.currency.crypto.CryptoCurrencyProvider;
import pl.muybien.notifier.currency.crypto.CryptoTarget;
import pl.muybien.notifier.customer.Customer;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class BitcoinService implements CurrencyService {

    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CryptoCurrencyComparator cryptoCurrencyComparator;
    private final BitcoinRepository bitcoinRepository;

    private final String uri = "bitcoin";

    @Scheduled(fixedRate = 10000)
    @Transactional
    protected void fetchCurrentStock() {
        var currentCrypto = cryptoCurrencyProvider.findCurrencyByUri(uri);
        var subscribers = bitcoinRepository.findAll()
                .stream()
                .map(bitcoin -> (CryptoTarget) bitcoin)
                .toList();
        System.out.println("Current crypto is not null"); // TODO: remove
        cryptoCurrencyComparator.updateValueAndCompareWithSubscribersGoals(currentCrypto, subscribers);
    }

    @Override
    public void createAndSaveSubscription(Customer customer, String cryptoName, BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var bitcoin = Bitcoin.builder()
                .customer(customer)
                .name(cryptoName)
                .upperBoundPrice(upperPriceInUsd)
                .lowerBoundPrice(lowerPriceInUsd)
                .build();
        bitcoinRepository.save(bitcoin);
    }
}
