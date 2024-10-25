package pl.muybien.marketsync.currency;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface CurrencyService {

    void fetchCurrentStock();
    void createAndSaveSubscription(Customer customer, String currencyName,
                                                   BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd);

    void removeSubscription(OidcUser oidcUser, Long id);
}
