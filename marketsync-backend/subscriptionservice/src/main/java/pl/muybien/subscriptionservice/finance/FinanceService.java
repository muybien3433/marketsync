package pl.muybien.subscriptionservice.finance;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.math.BigDecimal;

public interface FinanceService {

    void fetchCurrentFinanceAndCompare();
    void createAndSaveSubscription(String customerEmail,
                                   BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd);

    void removeSubscription(OidcUser oidcUser, Long id);
}
