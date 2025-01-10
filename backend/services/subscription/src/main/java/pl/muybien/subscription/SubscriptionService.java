package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.customer.CustomerClient;
import pl.muybien.exception.OwnershipException;
import pl.muybien.exception.SubscriptionNotFoundException;
import pl.muybien.finance.FinanceClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final CustomerClient customerClient;
    private final FinanceClient financeClient;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionDetailDTOMapper detailDTOMapper;

    private final MongoTemplate mongoTemplate;

    @Transactional
    public void createIncreaseSubscription(String authHeader, SubscriptionRequest request) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();
        var finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());

        var subscription = subscriptionRepository.findByUri(request.uri().trim().toLowerCase())
                .orElseGet(Subscription::new);

        var subscriptionDetail = SubscriptionDetail.builder()
                .id(UUID.randomUUID().toString())
                .customerId(customerId)
                .financeName(finance.name())
                .upperBoundPrice(request.value())
                .lowerBoundPrice(null)
                .assetType(finance.assetType())
                .notificationType(request.notificationType())
                .createdDate(LocalDateTime.now())
                .build();

        subscription.getSubscriptions()
                .computeIfAbsent(request.uri().trim().toLowerCase(), _ -> new LinkedList<>())
                .add(subscriptionDetail);

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void createDecreaseSubscription(String authHeader, SubscriptionRequest request) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        var finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());

        var subscription = subscriptionRepository.findByUri(request.uri().trim().toLowerCase())
                .orElseGet(Subscription::new);

        var subscriptionDetail = SubscriptionDetail.builder()
                .customerId(customer.id())
                .customerEmail(customer.email())
                .financeName(finance.name())
                .upperBoundPrice(null)
                .lowerBoundPrice(request.value())
                .assetType(finance.assetType())
                .notificationType(request.notificationType())
                .createdDate(LocalDateTime.now())
                .build();

        subscription.getSubscriptions()
                .computeIfAbsent(finance.name().trim().toLowerCase(), _ -> new LinkedList<>())
                .add(subscriptionDetail);

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void deleteSubscription(String authHeader, SubscriptionDeletionRequest request) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();

        var subscription = subscriptionRepository.findByUri(request.uri())
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for URI: " + request.uri()));

        var subscriptionDetailList = subscription.getSubscriptions().get(request.uri());
        if (subscriptionDetailList == null || subscriptionDetailList.isEmpty()) {
            throw new SubscriptionNotFoundException("Subscription not found for URI: " + request.uri());
        }

        var subscriptionDetail = subscriptionDetailList.stream()
                .filter(detail -> detail.getId().equals(request.id()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for ID: " + request.id()));

        if (!Objects.equals(subscriptionDetail.getCustomerId(), customerId)) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }

        subscriptionDetailList.remove(subscriptionDetail);
        subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDetailDTO> findAllCustomerSubscriptions(String authHeader) {
        var customerId = customerClient.fetchCustomerFromHeader(authHeader).id();

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("subscriptions").exists(true)),
                Aggregation.project()
                        .andExpression("{$objectToArray: '$subscriptions'}").as("subscriptionEntries"),
                Aggregation.unwind("subscriptionEntries"),
                Aggregation.unwind("subscriptionEntries.v"),
                Aggregation.match(Criteria.where("subscriptionEntries.v.customerId").is(customerId)),

                Aggregation.project()
                        .and("subscriptionEntries.k").as("assetType")
                        .and("subscriptionEntries.v.financeName").as("financeName")
                        .and("subscriptionEntries.v.upperBoundPrice").as("upperBoundPrice")
                        .and("subscriptionEntries.v.lowerBoundPrice").as("lowerBoundPrice")
                        .and("subscriptionEntries.v.createdDate").as("createdDate")
                        .and("subscriptionEntries.v.customerId").as("customerId")
        );

        var results = mongoTemplate.aggregate(aggregation, Subscription.class, SubscriptionDetail.class);

        return results.getMappedResults().stream()
                .map(detailDTOMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}
