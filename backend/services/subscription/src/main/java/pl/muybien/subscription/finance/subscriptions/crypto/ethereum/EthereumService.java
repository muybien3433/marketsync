package pl.muybien.subscription.finance.subscriptions.crypto.ethereum;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscription.exception.FinanceNotFoundException;
import pl.muybien.subscription.exception.OwnershipException;
import pl.muybien.subscription.finance.FinanceClient;
import pl.muybien.subscription.finance.FinanceComparator;
import pl.muybien.subscription.finance.FinanceService;
import pl.muybien.subscription.subscription.SubscriptionDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("ethereum")
@Transactional
@RequiredArgsConstructor
public class EthereumService implements FinanceService {

    private final FinanceComparator financeComparator;
    private final EthereumRepository repository;
    private final FinanceClient financeClient;

    @Value("${crypto.api.ethereum.uri}")
    private String name;

    @Override
    @Scheduled(fixedRateString = "${crypto.api.ethereum.fetch-time-ms}")
    public void fetchCurrentFinanceAndCompare() {
        var finance = financeClient.findFinanceByUri(name.toLowerCase());
        if (finance == null) {
            throw new FinanceNotFoundException("Finance not found for %s".formatted(name));
        }

        var subscriptions = repository.findAll();
        subscriptions.forEach(subscription -> {
                    if (financeComparator.priceMetSubscriptionCondition(finance.price(), subscription)) {
                        repository.delete(subscription);
                    }
                }
        );
    }

    @Override
    public SubscriptionDetail createIncreaseSubscription(BigDecimal value, String customerEmail, String customerId) {
        var subscription = Ethereum.builder()
                .financeName(name)
                .upperBoundPrice(value)
                .lowerBoundPrice(null)
                .customerEmail(customerEmail)
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .build();

        repository.save(subscription);

        return SubscriptionDetail.builder()
                .id(subscription.getId())
                .financeName(subscription.getFinanceName())
                .upperBoundPrice(subscription.getUpperBoundPrice())
                .lowerBoundPrice(subscription.getLowerBoundPrice())
                .customerId(customerId)
                .createdDate(subscription.getCreatedDate())
                .build();
    }

    @Override
    public SubscriptionDetail createDecreaseSubscription(BigDecimal value, String customerEmail, String customerId) {
        var subscription = Ethereum.builder()
                .financeName(name)
                .upperBoundPrice(null)
                .lowerBoundPrice(value)
                .customerEmail(customerEmail)
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .build();

        repository.save(subscription);

        return SubscriptionDetail.builder()
                .id(subscription.getId())
                .financeName(subscription.getFinanceName())
                .upperBoundPrice(subscription.getUpperBoundPrice())
                .lowerBoundPrice(subscription.getLowerBoundPrice())
                .createdDate(subscription.getCreatedDate())
                .build();
    }

    @Override
    public void deleteSubscription(Long subscriptionId, String customerId) {
        var subscription = repository.findById(subscriptionId).orElseThrow(() ->
                new EntityNotFoundException("Subscription with id %s not found".formatted(subscriptionId)));

        if (!subscription.getCustomerId().equals(customerId)) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }
        repository.delete(subscription);
    }
}
