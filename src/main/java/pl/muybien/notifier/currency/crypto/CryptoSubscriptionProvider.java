package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.notifier.currency.CurrencyService;
import pl.muybien.notifier.customer.Customer;
import pl.muybien.notifier.customer.CustomerService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class CryptoSubscriptionProvider {

    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CustomerService customerService;
    private final CurrencyService currencyService;

    @Transactional
    public void addSubscription(OidcUser oidcUser, String uri,
                                Double upperValueInPercent, Double lowerValueInPercent) {
        Crypto currentCrypto = cryptoCurrencyProvider.findCurrencyByUri(uri);
        String cryptoName = currentCrypto.getName();
        BigDecimal currentCryptoPrice = currentCrypto.getPriceUsd();
        BigDecimal upperPriceInUsd = calculatePriceByClientPercentInput(currentCryptoPrice, upperValueInPercent);
        BigDecimal lowerPriceInUsd = calculatePriceByClientPercentInput(currentCryptoPrice, lowerValueInPercent);

        Customer customer = customerService.findCustomerByEmail(oidcUser.getEmail());

        currencyService.createAndSaveSubscription(customer, cryptoName, upperPriceInUsd, lowerPriceInUsd);
    }

    private BigDecimal calculatePriceByClientPercentInput(BigDecimal currentCryptoPrice, Double valueInPercent) {
        BigDecimal percentDecimal = new BigDecimal(valueInPercent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal change = currentCryptoPrice.multiply(percentDecimal);
        return currentCryptoPrice.add(change);
    }
}
