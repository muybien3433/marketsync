package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.notifier.customer.Customer;
import pl.muybien.notifier.customer.CustomerService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class CryptoSubscriptionManager {

    private final CryptoServiceFactory cryptoServiceFactory;
    private final CryptoCurrencyProvider cryptoCurrencyProvider;
    private final CustomerService customerService;

    @Transactional
    public void addSubscription(OidcUser oidcUser, String uri,
                                Double upperValueInPercent, Double lowerValueInPercent) {
        CryptoService cryptoService = cryptoServiceFactory.getService(uri);
        Crypto currentCrypto = cryptoCurrencyProvider.fetchCurrencyByUri(uri);
        String cryptoName = currentCrypto.getName();
        BigDecimal currentCryptoPrice = currentCrypto.getPriceUsd();
        BigDecimal upperPriceInUsd = calculatePriceByClientPercentInput(currentCryptoPrice, upperValueInPercent);
        BigDecimal lowerPriceInUsd = calculatePriceByClientPercentInput(currentCryptoPrice, lowerValueInPercent);

        Customer customer = customerService.findCustomerByEmail(oidcUser.getEmail());

        cryptoService.createAndSaveSubscription(customer, cryptoName, upperPriceInUsd, lowerPriceInUsd);
    }

    private BigDecimal calculatePriceByClientPercentInput(BigDecimal currentCryptoPrice, Double valueInPercent) {
        BigDecimal percentDecimal = new BigDecimal(valueInPercent).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal change = currentCryptoPrice.multiply(percentDecimal);
        return currentCryptoPrice.add(change);
    }

    @Transactional
    public void removeSubscription(CryptoTarget cryptoTarget, String uri) {
        CryptoService cryptoService = cryptoServiceFactory.getService(uri);
        cryptoService.removeSubscription(cryptoTarget);
    }
}