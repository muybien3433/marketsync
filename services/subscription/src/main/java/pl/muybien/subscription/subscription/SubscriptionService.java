package pl.muybien.subscription.subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscription.customer.CustomerClient;
import pl.muybien.subscription.exception.BusinessException;
import pl.muybien.subscription.exception.OwnershipException;
import pl.muybien.subscription.finance.FinanceServiceFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final FinanceServiceFactory financeServiceFactory;
    private final CustomerClient customerClient;
    private final SubscriptionRepository repository;
    private final SubscriptionDetailDTOMapper detailDTOMapper;

    @Transactional
    protected SubscriptionDetailDTO createIncreaseSubscription(SubscriptionRequest request) {
        var customer = customerClient.findCustomerById(request.customerId()).orElseThrow(() ->
                new BusinessException("Subscription not created:: No Customer exists with ID: %d"
                        .formatted(request.customerId())));

        var service = financeServiceFactory.getService(request.uri());
        var subscription = findOrCreateSubscription(request);
        var subscriptionDetail = service.createIncreaseSubscription(request.value(), customer.id());

        subscription.getSubscriptionDetails().add(subscriptionDetail);
        repository.save(subscription);

        return SubscriptionDetailDTO.builder()
                .financeName(subscriptionDetail.getFinanceName())
                .upperBoundPrice(subscriptionDetail.getUpperBoundPrice())
                .lowerBoundPrice(subscriptionDetail.getLowerBoundPrice())
                .createdDate(subscriptionDetail.getCreatedDate())
                .build();
    }

    @Transactional
    protected SubscriptionDetailDTO createDecreaseSubscription(SubscriptionRequest request) {
        var customer = customerClient.findCustomerById(request.customerId()).orElseThrow(() ->
                new BusinessException("Subscription not created:: No Customer exists with ID: %d"
                        .formatted(request.customerId())));

        var service = financeServiceFactory.getService(request.uri());
        var subscription = findOrCreateSubscription(request);
        var subscriptionDetail = service.createDecreaseSubscription(request.value(), customer.id());

        subscription.getSubscriptionDetails().add(subscriptionDetail);
        repository.save(subscription);

        return SubscriptionDetailDTO.builder()
                .financeName(subscriptionDetail.getFinanceName())
                .upperBoundPrice(subscriptionDetail.getUpperBoundPrice())
                .lowerBoundPrice(subscriptionDetail.getLowerBoundPrice())
                .createdDate(subscriptionDetail.getCreatedDate())
                .build();
    }

    @Transactional
    protected Subscription findOrCreateSubscription(SubscriptionRequest request) {
        return repository.findByCustomerId(request.customerId()).orElse(
                Subscription.builder()
                        .subscriptionDetails(new ArrayList<>())
                        .createdDate(LocalDateTime.now())
                        .build());
    }

    @Transactional
    protected void deleteSubscription(SubscriptionDeletionRequest request) {
        var service = financeServiceFactory.getService(request.uri());
        service.deleteSubscription(request.subscriptionId(), request.customerId());

        var subscription = repository.findByCustomerId(request.customerId()).orElseThrow(() ->
                new EntityNotFoundException("Subscription not found"));

        if (!subscription.getCustomerId().equals(request.customerId())) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }

        boolean removed = subscription.getSubscriptionDetails().removeIf(detail ->
                detail.getId().equals(request.subscriptionId()));

        if (!removed) {
            throw new EntityNotFoundException("Subscription not found");
        }

        repository.save(subscription);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDetailDTO> findAllSubscriptions(Long customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .flatMap(s -> s.getSubscriptionDetails()
                        .stream()
                        .map(detailDTOMapper::mapToDTO))
                .sorted((s1, s2) -> s2.createdDate().compareTo(s1.createdDate()))
                .collect(Collectors.toList());
    }
}
