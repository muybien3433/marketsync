package pl.muybien.subscriptionservice.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscriptionservice.finance.FinanceService;
import pl.muybien.subscriptionservice.finance.FinanceServiceFactory;
import pl.muybien.subscriptionservice.handler.InvalidSubscriptionParametersException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final FinanceServiceFactory financeServiceFactory;

    @Transactional
    public void createIncreaseSubscription(OidcUser oidcUser, String uri, BigDecimal value) {
        var service = financeServiceFactory.getService(uri);

        if (value != null) {
            service.createAndSaveSubscription(oidcUser.getEmail(), value, null);
        } else {
            throw new InvalidSubscriptionParametersException("Value is required and must be grater than zero.");
        }
    }

    @Transactional
    public void createDecreaseSubscription(OidcUser oidcUser, String uri, BigDecimal value) {
        var service = financeServiceFactory.getService(uri);

        if (value != null) {
            service.createAndSaveSubscription(oidcUser.getEmail(), null, value);
        } else {
            throw new InvalidSubscriptionParametersException("Value is required and must be grater than zero.");
        }
    }

    @Transactional
    public void removeSubscription(OidcUser oidcUser, String uri, Long id) {
        FinanceService financeService = financeServiceFactory.getService(uri);
        financeService.removeSubscription(oidcUser, id);
    }
}
