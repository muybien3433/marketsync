package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.exception.OwnershipException;
import pl.muybien.exception.SubscriptionNotFoundException;
import pl.muybien.finance.FinanceClient;
import pl.muybien.finance.FinanceResponse;
import pl.muybien.subscription.data.Subscription;
import pl.muybien.subscription.data.SubscriptionDetail;
import pl.muybien.subscription.data.SubscriptionRepository;
import pl.muybien.subscription.dto.SubscriptionDetailDTO;
import pl.muybien.subscription.dto.SubscriptionDetailDTOMapper;
import pl.muybien.subscription.request.SubscriptionDeletionRequest;
import pl.muybien.subscription.request.SubscriptionRequest;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final FinanceClient financeClient;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionDetailDTOMapper detailDTOMapper;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public void createIncreaseSubscription(String customerId, String customerEmail, SubscriptionRequest request) {
        var finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());
        var subscription = subscriptionRepository.findByUri(request.uri().trim().toLowerCase())
                .orElseGet(Subscription::new);

        BigDecimal value = resolveValueByCurrency(request, finance);

        var subscriptionDetail = SubscriptionDetail.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .customerEmail(customerEmail)
                .financeName(finance.name())
                .requestedValue(request.value())
                .requestedCurrency(request.currency())
                .upperBoundPrice(value.doubleValue())
                .lowerBoundPrice(null)
                .assetType(finance.assetType())
                .notificationType(request.notificationType())
                .build();

        subscription.getSubscriptions()
                .computeIfAbsent(finance.name().trim().toLowerCase(), _ -> new LinkedList<>())
                .add(subscriptionDetail);

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void createDecreaseSubscription(String customerId, String customerEmail, SubscriptionRequest request) {
        var finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());
        var subscription = subscriptionRepository.findByUri(request.uri().trim().toLowerCase())
                .orElseGet(Subscription::new);

        BigDecimal value = resolveValueByCurrency(request, finance);

        var subscriptionDetail = SubscriptionDetail.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .customerEmail(customerEmail)
                .financeName(finance.name())
                .requestedValue(request.value())
                .requestedCurrency(request.currency())
                .upperBoundPrice(null)
                .lowerBoundPrice(value.doubleValue())
                .assetType(finance.assetType())
                .notificationType(request.notificationType())
                .build();

        subscription.getSubscriptions()
                .computeIfAbsent(finance.name().trim().toLowerCase(), _ -> new LinkedList<>())
                .add(subscriptionDetail);

        subscriptionRepository.save(subscription);
    }

    private BigDecimal resolveValueByCurrency(SubscriptionRequest request, FinanceResponse finance) {
        BigDecimal value = BigDecimal.valueOf(request.value());

        if (!request.currency().equals(finance.currency())) {
            BigDecimal exchange = financeClient.findExchangeRate(request.currency(), finance.currency());
            value = BigDecimal.valueOf(request.value()).multiply(exchange);
        }
        return value;
    }

    @Transactional
    public void deleteSubscription(String customerId, SubscriptionDeletionRequest request) {
        var subscription = subscriptionRepository.findByUri(request.uri())
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for URI: " + request.uri()));

        var subscriptionDetailList = Optional.ofNullable(subscription.getSubscriptions().get(request.uri()))
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for URI: " + request.uri()));

        var subscriptionDetail = subscriptionDetailList.stream()
                .filter(detail -> detail.getId().equals(request.id()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for ID: " + request.id()));

        if (!subscriptionDetail.getCustomerId().equals(customerId)) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }

        subscriptionDetailList.remove(subscriptionDetail);
        subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDetailDTO> findAllCustomerSubscriptions(String customerId) {
        var aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("subscriptions").exists(true)),
                Aggregation.project()
                        .andExpression("{$objectToArray: '$subscriptions'}").as("subscriptionEntries"),
                Aggregation.unwind("subscriptionEntries"),
                Aggregation.unwind("subscriptionEntries.v"),
                Aggregation.match(Criteria.where("subscriptionEntries.v.customerId").is(customerId)),

                Aggregation.project()
                        .and("subscriptionEntries.k").as("type")
                        .and("subscriptionEntries.v.financeName").as("financeName")
                        .and("subscriptionEntries.v.upperBoundPrice").as("upperBoundPrice")
                        .and("subscriptionEntries.v.lowerBoundPrice").as("lowerBoundPrice")
                        .and("subscriptionEntries.v.createdDate").as("createdDate")
                        .and("subscriptionEntries.v.customerId").as("customerId")
        );

        var results = mongoTemplate.aggregate(aggregation, Subscription.class, SubscriptionDetail.class);

        return results.getMappedResults().stream()
                .map(detailDTOMapper::toDTO)
                .collect(Collectors.toList());
    }
}
