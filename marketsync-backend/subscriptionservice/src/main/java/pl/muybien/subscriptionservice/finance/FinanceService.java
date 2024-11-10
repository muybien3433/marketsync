package pl.muybien.subscriptionservice.finance;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import pl.muybien.marketsync.customer.Customer;

import java.math.BigDecimal;

public interface FinanceService {

    void fetchCurrentFinance();
    void createAndSaveSubscription(Customer customer, String financeName,
                                                   BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd);

    void removeSubscription(OidcUser oidcUser, Long id);
}
