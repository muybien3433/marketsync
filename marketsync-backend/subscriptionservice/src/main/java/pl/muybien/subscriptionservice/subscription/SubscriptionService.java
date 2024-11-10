package pl.muybien.subscriptionservice.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscriptionservice.finance.FinanceProviderFactory;
import pl.muybien.subscriptionservice.finance.FinanceService;
import pl.muybien.subscriptionservice.finance.FinanceServiceFactory;
import pl.muybien.subscriptionservice.handler.InvalidSubscriptionParametersException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final FinanceServiceFactory financeServiceFactory;
    private final FinanceProviderFactory financeProviderFactory;

    @Transactional
    public void addIncreaseSubscription(OidcUser oidcUser, String uri, BigDecimal value) {
        var service = financeServiceFactory.getService(uri);
        // TODO: After marketplace creation provide necessarily logic switching between providers
        var financeProvider = financeProviderFactory.getProvider("crypto");
        String financeName = financeProvider.fetchFinance(uri).getName();

        if (value != null) {
            service.createAndSaveSubscription(oidcUser.getEmail(), financeName, value, null);
        } else {
            throw new InvalidSubscriptionParametersException("Value is required and must be grater than zero.");
        }
    }

    @Transactional
    public void addDecreaseSubscription(OidcUser oidcUser, String uri, BigDecimal value) {
        var service = financeServiceFactory.getService(uri);
        // TODO: After marketplace creation provide necessarily logic switching between providers
        var financeProvider = financeProviderFactory.getProvider("crypto");
        String financeName = financeProvider.fetchFinance(uri).getName();

        if (value != null) {
            service.createAndSaveSubscription(oidcUser.getEmail(), financeName, null, value);
        } else {
            throw new InvalidSubscriptionParametersException("Value is required.");
        }
    }

    @Transactional
    public void removeSubscription(OidcUser oidcUser, String uri, Long id) {
        FinanceService financeService = financeServiceFactory.getService(uri);
        financeService.removeSubscription(oidcUser, id);
    }
}
