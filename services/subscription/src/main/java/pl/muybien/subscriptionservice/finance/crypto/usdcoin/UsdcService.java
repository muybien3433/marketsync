package pl.muybien.subscriptionservice.finance.crypto.usdcoin;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.subscriptionservice.finance.FinanceComparator;
import pl.muybien.subscriptionservice.finance.FinanceService;
import pl.muybien.subscriptionservice.handler.FinanceNotFoundException;
import pl.muybien.subscriptionservice.subscription.SubscriptionListManager;

import java.math.BigDecimal;

@Service("usd-coin")
@Transactional
@RequiredArgsConstructor
public class UsdcService implements FinanceService {

    private final FinanceComparator financeComparator;
    private final SubscriptionListManager subscriptionListManager;
    private final UsdcRepository repository;
    private final WebClient.Builder webClientBuilder;

    @Value("${usd-coin.api.url}")
    private String url;

    @Override
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void fetchCurrentFinanceAndCompare() {
        WebClient webClient = webClientBuilder.baseUrl(url).build();
        UsdcResponse response = webClient.get().retrieve().bodyToMono(UsdcResponse.class).block();

        if (response != null) {
            var subscriptions = repository.findAll();
            subscriptions.forEach(subscription -> {
                if (financeComparator.priceMetSubscriptionCondition(response.priceUsd(), subscription)) {
                    repository.delete(subscription);
                }
            });
        } else {
            throw new FinanceNotFoundException("Data not found for URL: %s".formatted(url));
        }
    }

    @Override
    @Transactional
    public void createAndSaveSubscription(String customerEmail,
                                          BigDecimal upperPriceInUsd, BigDecimal lowerPriceInUsd) {
        var crypto = Usdc.builder()
                .customerEmail(customerEmail)
                .name("usd-coin")
                .upperBoundPrice(upperPriceInUsd)
                .lowerBoundPrice(lowerPriceInUsd)
                .build();
        repository.save(crypto);
        subscriptionListManager.addSubscriptionToList(crypto);
    }

    @Override
    @Transactional
    public void removeSubscription(OidcUser oidcUser, Long id) {
        repository.findById(id).ifPresentOrElse(crypto -> {
            if (crypto.getCustomerEmail().equals(oidcUser.getEmail())) {
                repository.delete(crypto);
                subscriptionListManager.removeSubscriptionFromList(crypto);
            } else {
                throw new AccessDeniedException("You are not authorized to delete this subscription.");
            }
        }, () -> {
            throw new EntityNotFoundException("Subscription with id %d not found.".formatted(id));
        });
    }
}
