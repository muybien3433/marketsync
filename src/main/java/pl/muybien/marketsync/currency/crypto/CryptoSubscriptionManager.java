package pl.muybien.marketsync.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.customer.CustomerService;
import pl.muybien.marketsync.handler.InvalidSubscriptionParametersException;

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
        var cryptoService = cryptoServiceFactory.getService(uri);
        var currentCrypto = cryptoCurrencyProvider.fetchCurrencyByUri(uri);
        String cryptoName = currentCrypto.getName();
        BigDecimal currentCryptoPrice = currentCrypto.getPriceUsd();

        BigDecimal upperPriceInUsd = null;
        if (upperValueInPercent != null) {
            upperPriceInUsd = calculatePriceByClientPercentInput(currentCryptoPrice, upperValueInPercent);
        }

        BigDecimal lowerPriceInUsd = null;
        if (lowerValueInPercent != null) {
            lowerPriceInUsd = calculatePriceByClientPercentInput(currentCryptoPrice, lowerValueInPercent);
        }

        Customer customer = customerService.findCustomerByEmail(oidcUser.getEmail());

        if (upperValueInPercent != null || lowerValueInPercent != null) {
            cryptoService.createAndSaveSubscription(customer, cryptoName, upperPriceInUsd, lowerPriceInUsd);
        } else {
            throw new InvalidSubscriptionParametersException("At least one parameter must be provided.");
        }
    }

    private BigDecimal calculatePriceByClientPercentInput(BigDecimal currentCryptoPrice, Double valueInPercent) {
        BigDecimal percentDecimal = new BigDecimal(valueInPercent)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal change = currentCryptoPrice.multiply(percentDecimal);

        return currentCryptoPrice.add(change).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void removeSubscription(OidcUser oidcUser, String uri, Long id) {
        CryptoService cryptoService = cryptoServiceFactory.getService(uri);
        cryptoService.removeSubscription(oidcUser, id);
    }
}