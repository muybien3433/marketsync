package pl.muybien.subscription.subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscription.customer.CustomerClient;
import pl.muybien.subscription.exception.CustomerNotFoundException;
import pl.muybien.subscription.exception.OwnershipException;
import pl.muybien.subscription.exception.SubscriptionNotFoundException;
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
    protected SubscriptionDetailDTO createIncreaseSubscription(String authHeader, SubscriptionRequest request) {
        var customer = customerClient.findCustomerById(authHeader, request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Subscription not created:: No Customer exists with ID: %d"
                                .formatted(request.customerId())));

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new SubscriptionNotFoundException(
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
    protected SubscriptionDetailDTO createDecreaseSubscription(String authHeader, SubscriptionRequest request) {
        var customer = customerClient.findCustomerById(authHeader, request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Subscription not created:: No Customer exists with ID: %d"
                                .formatted(request.customerId())));

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new SubscriptionNotFoundException(
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
    protected void deleteSubscription(String authHeader, SubscriptionDeletionRequest request) {
        var customer = customerClient.findCustomerById(authHeader, request.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Subscription not deleted:: No Customer exists with ID: %d".formatted(request.customerId())));

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new SubscriptionNotFoundException(
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
    public List<SubscriptionDetailDTO> findAllSubscriptions(String authHeader, Long customerId) {
        var customer = customerClient.findCustomerById(authHeader, customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Subscription not found:: No Customer exists with ID: %d".formatted(customerId)));

        return subscriptionRepository.findByCustomerId(customer.id())
                .stream()
                .flatMap(s -> s.getSubscriptionDetails()
                        .stream()
                        .map(detailDTOMapper::mapToDTO))
                .sorted((s1, s2) -> s2.createdDate().compareTo(s1.createdDate()))
                .collect(Collectors.toList());
    }
}
