package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.enums.NotificationType;
import pl.muybien.exception.InvalidSubscriptionParametersException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.exception.SubscriptionNotFoundException;
import pl.muybien.finance.FinanceClient;
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
        } else if (request.upperBoundPrice() != null && request.lowerBoundPrice() != null) {
            throw new InvalidSubscriptionParametersException("Only one, upper or lower bound price can be specified");
        }

        String uri = request.uri().trim().toLowerCase();
        var finance = financeClient.findFinanceByTypeAndUri(request.assetType(), request.uri());
        var subscription = subscriptionRepository.findByUri(uri)
                .orElseGet(() -> {
                            var newSub = new Subscription();
                            newSub.setUri(uri);
                            return newSub;
                        }
                );

        String target = resolveTargetByNotificationType(request.notificationType(), customerEmail, phoneNumber);

        var subscriptionDetail = new SubscriptionDetail(
                UUID.randomUUID().toString(),
                request.uri(),
                customerId,
                target,
                finance.name(),
                request.currencyType(),
                request.upperBoundPrice(),
                request.lowerBoundPrice(),
                finance.assetType(),
                request.notificationType(),
                LocalDateTime.now()
        );

        subscription.getSubscriptionDetails().add(subscriptionDetail);
        subscriptionRepository.save(subscription);
    }

    private String resolveTargetByNotificationType(NotificationType notificationType, String customerEmail, String phoneNumber) {
        switch (notificationType) {
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
    public void deleteSubscription(String customerId, String uri, String id) {
        var subscription = subscriptionRepository.findByUri(uri)
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
                .map(sDetail -> detailDTOMapper.toDTO(sDetail, resolveCurrentPrice(sDetail)))
                .collect(Collectors.toList());
    }

    String resolveCurrentPrice(SubscriptionDetail subscriptionDetail) {
        var finance = financeClient
                .findFinanceByTypeAndUri(subscriptionDetail.assetType(), subscriptionDetail.uri());

        String currentPrice = finance.price();
        if (!finance.currencyType().equals(subscriptionDetail.requestedCurrency())) {
            var exchange = financeClient
                    .findExchangeRate(finance.currencyType(), subscriptionDetail.requestedCurrency());

            currentPrice = new BigDecimal(currentPrice).multiply(exchange).toPlainString();
        }
        return currentPrice;
    }
}
