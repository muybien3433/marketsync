package pl.muybien.subscription.subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscription.customer.CustomerClient;
import pl.muybien.subscription.exception.BusinessException;
import pl.muybien.subscription.exception.OwnershipException;
import pl.muybien.subscription.finance.FinanceServiceFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final FinanceServiceFactory financeServiceFactory;
    private final CustomerClient customerClient;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionDetailRepository subscriptionDetailRepository;
    private final SubscriptionDetailDTOMapper detailDTOMapper;

    @Transactional
    protected SubscriptionDetailDTO createIncreaseSubscription(SubscriptionRequest request, String authorizationHeader) {
        var customer = customerClient.findCustomerById(authorizationHeader, request.customerId())
                .orElseThrow(() -> new BusinessException(
                        "Subscription not created:: No Customer exists with ID: %d"
                                .formatted(request.customerId())));

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new BusinessException(
                        "Subscription not created:: No Customer exists with ID: %d"
                                .formatted(request.customerId())));

        var financeService = financeServiceFactory.getService(request.uri());
        var createdSubscription = financeService.createIncreaseSubscription(
                request.value(), customer.email(), customer.id());

        createdSubscription.setSubscription(subscription);

        subscriptionDetailRepository.save(createdSubscription);

        return SubscriptionDetailDTO.builder()
                .financeName(createdSubscription.getFinanceName())
                .upperBoundPrice(createdSubscription.getUpperBoundPrice())
                .lowerBoundPrice(createdSubscription.getLowerBoundPrice())
                .createdDate(createdSubscription.getCreatedDate())
                .build();
    }

    @Transactional
    protected SubscriptionDetailDTO createDecreaseSubscription(SubscriptionRequest request, String authorizationHeader) {
        var customer = customerClient.findCustomerById(authorizationHeader, request.customerId())
                .orElseThrow(() -> new BusinessException(
                        "Subscription not created:: No Customer exists with ID: %d"
                                .formatted(request.customerId())));

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new BusinessException(
                        "Subscription not created:: No Customer exists with ID: %d"
                                .formatted(request.customerId())));

        var financeService = financeServiceFactory.getService(request.uri());
        var createdSubscription = financeService.createDecreaseSubscription(
                request.value(), customer.email(), customer.id());

        createdSubscription.setSubscription(subscription);

        subscriptionDetailRepository.save(createdSubscription);

        return SubscriptionDetailDTO.builder()
                .financeName(createdSubscription.getFinanceName())
                .upperBoundPrice(createdSubscription.getUpperBoundPrice())
                .lowerBoundPrice(createdSubscription.getLowerBoundPrice())
                .createdDate(createdSubscription.getCreatedDate())
                .build();
    }

    @Transactional
    protected void deleteSubscription(SubscriptionDeletionRequest request, String authorizationHeader) {
        var customer = customerClient.findCustomerById(authorizationHeader, request.customerId())
                .orElseThrow(() -> new BusinessException(
                        "Subscription not deleted:: No Customer exists with ID: %d".formatted(request.customerId())));

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new BusinessException(
                        "Subscription not deleted:: No Subscription exists with ID: %d"
                                .formatted(request.customerId())));

        var subscriptionDetail = subscriptionDetailRepository.findById(request.subscriptionDetailId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subscription not deleted:: No Subscription exists with ID: %d".
                                formatted(request.subscriptionDetailId())));

        if (!subscriptionDetail.getCustomerId().equals(customer.id())) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }

        var financeService = financeServiceFactory.getService(subscriptionDetail.getFinanceName());
        financeService.deleteSubscription(subscription.getId(), customer.id());

        subscriptionDetailRepository.delete(subscriptionDetail);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDetailDTO> findAllSubscriptions(Long customerId) {
        return subscriptionRepository.findByCustomerId(customerId)
                .stream()
                .flatMap(s -> s.getSubscriptionDetails()
                        .stream()
                        .map(detailDTOMapper::mapToDTO))
                .sorted((s1, s2) -> s2.createdDate().compareTo(s1.createdDate()))
                .collect(Collectors.toList());
    }
}
