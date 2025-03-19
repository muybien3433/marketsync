package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void createSubscription(
            String customerId, String customerEmail, String phoneNumber, SubscriptionRequest request) {

        if (request.upperBoundPrice() == null && request.lowerBoundPrice() == null) {
            throw new InvalidSubscriptionParametersException("Upper or lower bound price is mandatory");
        }

        String uri = request.uri().trim().toLowerCase();
        var finance = financeClient.findFinanceByAssetTypeAndUri(request.assetType(), request.uri());
        var subscription = subscriptionRepository.findByUri(uri)
                .orElseGet(() -> {
                            var newSub = new Subscription();
                            newSub.setUri(uri);
                            return newSub;
                        }
                );

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

        subscription.getSubscriptionDetails().add(subscriptionDetail);
        subscriptionRepository.save(subscription);
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
    public void deleteSubscription(String customerId, String id) {
        var subscription = subscriptionRepository.findByDetailId(id)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for id: " + id));

        var subscriptionDetail = subscription.getSubscriptionDetails().stream()
                .filter(d -> d.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription detail not found"));

        if (!subscriptionDetail.customerId().equals(customerId)) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }

        subscription.getSubscriptionDetails().remove(subscriptionDetail);
        subscriptionRepository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDetailDTO> findAllCustomerSubscriptions(String customerId) {
        return subscriptionRepository.findAll().stream()
                .flatMap(s -> s.getSubscriptionDetails().stream()
                        .filter(d -> d.customerId().equals(customerId)))
                .map(detailDTOMapper::toDTO)
                .collect(Collectors.toList());
    }
}
