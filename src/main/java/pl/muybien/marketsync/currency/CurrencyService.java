package pl.muybien.marketsync.currency.crypto;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface CryptoService {

    void fetchCurrentStock();
    void createAndSaveSubscription(Customer customer, String cryptoName,
                                                   BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd);

    void removeSubscription(OidcUser oidcUser, Long id);
}
