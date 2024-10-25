package pl.muybien.marketsync.asset;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface AssetService {

    void fetchCurrentStock();
    void createAndSaveSubscription(Customer customer, String assetName,
                                                   BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd);

    void removeSubscription(OidcUser oidcUser, Long id);
}
