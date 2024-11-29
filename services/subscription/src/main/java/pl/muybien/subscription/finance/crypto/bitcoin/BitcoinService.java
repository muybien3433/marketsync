package pl.muybien.subscription.finance.crypto.bitcoin;

import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscription.exception.BusinessException;
import pl.muybien.subscription.exception.OwnershipException;
import pl.muybien.subscription.finance.FinanceClient;
import pl.muybien.subscription.finance.FinanceComparator;
import pl.muybien.subscription.finance.FinanceService;
import pl.muybien.subscription.subscription.SubscriptionDetail;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("bitcoin")
@Transactional
@RequiredArgsConstructor
public class BitcoinService implements FinanceService {

    private final FinanceComparator financeComparator;
    private final BitcoinRepository repository;
    private final FinanceClient financeClient;

    @Value("${api.bitcoin.uri}")
    private String name;

    @Override
    @Scheduled(fixedRateString = "${api.bitcoin.fetch-time-ms}")
    public void fetchCurrentFinanceAndCompare() {
        var financeResponse = financeClient.findFinanceByUri(name.toLowerCase()).orElseThrow(() ->
                new BusinessException("Finance not found for %s".formatted(name)));

        if (financeResponse != null) {
            var subscriptions = repository.findAll();
            subscriptions.forEach(subscription -> {
                if (financeComparator.priceMetSubscriptionCondition(financeResponse.priceUsd(), subscription)) {
                    repository.delete(subscription);
                }
            });
        }
    }

    @Override
    public SubscriptionDetail createIncreaseSubscription(BigDecimal value, String customerEmail, Long customerId) {
        var subscription = Bitcoin.builder()
                .financeName(name)
                .upperBoundPrice(value)
                .lowerBoundPrice(null)
                .customerId(customerId)
                .customerEmail(customerEmail)
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
    public SubscriptionDetail createDecreaseSubscription(BigDecimal value, String customerEmail, Long customerId) {
        var subscription = Bitcoin.builder()
                .financeName(name)
                .upperBoundPrice(null)
                .lowerBoundPrice(value)
                .customerEmail(customerEmail)
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .build();

        repository.save(subscription);

        return SubscriptionDetail.builder()
                .financeName(subscription.getFinanceName())
                .upperBoundPrice(subscription.getUpperBoundPrice())
                .lowerBoundPrice(subscription.getLowerBoundPrice())
                .createdDate(subscription.getCreatedDate())
                .build();
    }

    @Override
    public void deleteSubscription(Long subscriptionId, Long customerId) {
        var subscription = repository.findById(subscriptionId).orElseThrow(() ->
                new EntityNotFoundException("Subscription with id %s not found".formatted(subscriptionId)));

        if (!subscription.getCustomerId().equals(customerId)) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }
        repository.delete(subscription);
    }
}
