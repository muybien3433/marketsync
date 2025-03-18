package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.exception.InvalidSubscriptionParametersException;
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
import java.time.LocalDateTime;
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
    public void createSubscription(
            String customerId, String customerEmail, String phoneNumber, SubscriptionRequest request) {

        if (request.upperBoundPrice() == null && request.lowerBoundPrice() == null) {
            throw new InvalidSubscriptionParametersException("Upper or lower bound price is mandatory");
        }

        var finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());
        var existingSubscriptions = subscriptionRepository.findByUri(request.uri().trim().toLowerCase())
                .orElseGet(Subscription::new);

        Double upperBoundPrice = resolveBoundByCurrency(request.upperBoundPrice(), request.currencyType(), finance);
        Double lowerBoundPrice = resolveBoundByCurrency(request.lowerBoundPrice(), request.currencyType(), finance);
        String target = resolveTargetByNotificationType(request.notificationType(), customerEmail, phoneNumber);

        var subscriptionDetail = new SubscriptionDetail(
                UUID.randomUUID().toString(),
                request.uri(),
                customerId,
                target,
                finance.name(),
                CurrencyType.valueOf(request.currencyType()),
                upperBoundPrice,
                lowerBoundPrice,
                AssetType.valueOf(finance.assetType()),
                NotificationType.valueOf(request.notificationType()),
                LocalDateTime.now()
        );

        existingSubscriptions.getSubscriptions()
                .computeIfAbsent(finance.name().trim().toLowerCase(), _ -> new LinkedList<>())
                .add(subscriptionDetail);

        subscriptionRepository.save(existingSubscriptions);
    }
    
    private Double resolveBoundByCurrency(Double price, String currencyType, FinanceResponse finance) {
        if (price == null) {
            return null;
        }

        if (!currencyType.equals(finance.currency())) {
            BigDecimal exchange = financeClient.findExchangeRate(currencyType, finance.currency());
            price = BigDecimal.valueOf(price).multiply(exchange).doubleValue();
        }
        return price;
    }

    private String resolveTargetByNotificationType(String notificationType, String customerEmail, String phoneNumber) {
        switch (NotificationType.valueOf(notificationType)) {
            case EMAIL -> {
                if (customerEmail != null && !customerEmail.isEmpty()) {
                    return customerEmail;
                } else {
                    throw new InvalidSubscriptionParametersException("Email could not be resolved");
                }
            }
            case SMS -> {
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    return phoneNumber;
                } else {
                    throw new InvalidSubscriptionParametersException("Phone number could not be resolved");
                }
            }
            default -> throw new InvalidSubscriptionParametersException("Unknown notification type");
        }
    }

    @Transactional
    public void deleteSubscription(String customerId, SubscriptionDeletionRequest request) {
        var subscription = subscriptionRepository.findByUri(request.uri())
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for URI: " + request.uri()));

        var subscriptionDetailList = Optional.ofNullable(subscription.getSubscriptions().get(request.uri()))
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription detail list not found for URI: " + request.uri()));

        var subscriptionDetail = subscriptionDetailList.stream()
                .filter(detail -> detail.id().equals(request.id()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription detail not found for ID: " + request.id()));

        if (!subscriptionDetail.customerId().equals(customerId)) {
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
