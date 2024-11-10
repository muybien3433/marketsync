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
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final FinanceServiceFactory financeServiceFactory;
    private final FinanceProviderFactory financeProviderFactory;

    @Transactional
    public void addSubscription(OidcUser oidcUser, String uri,
                                Double upperValueInPercent, Double lowerValueInPercent) {
        var service = financeServiceFactory.getService(uri);
        // TODO: After marketplace creation provide necessarily logic switching between providers
        var financeProvider = financeProviderFactory.getProvider("crypto");
        var currentFinance = financeProvider.fetchFinance(uri);
        String financeName = currentFinance.getName();
        BigDecimal currentFinancePrice = currentFinance.getPriceUsd();

        BigDecimal upperPriceInUsd = null;
        if (upperValueInPercent != null) {
            upperPriceInUsd = calculatePriceByClientPercentInput(currentFinancePrice, upperValueInPercent);
        }

        BigDecimal lowerPriceInUsd = null;
        if (lowerValueInPercent != null) {
            lowerPriceInUsd = calculatePriceByClientPercentInput(currentFinancePrice, lowerValueInPercent);
        }

        if (upperValueInPercent != null || lowerValueInPercent != null) {
            service.createAndSaveSubscription(oidcUser.getEmail(), financeName, upperPriceInUsd, lowerPriceInUsd);
        } else {
            throw new InvalidSubscriptionParametersException("At least one parameter must be provided.");
        }
    }

    private BigDecimal calculatePriceByClientPercentInput(BigDecimal financePrice, Double valueInPercent) {
        BigDecimal percentDecimal = new BigDecimal(valueInPercent)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal change = financePrice.multiply(percentDecimal);

        return financePrice.add(change).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void removeSubscription(OidcUser oidcUser, String uri, Long id) {
        FinanceService financeService = financeServiceFactory.getService(uri);
        financeService.removeSubscription(oidcUser, id);
    }
}
