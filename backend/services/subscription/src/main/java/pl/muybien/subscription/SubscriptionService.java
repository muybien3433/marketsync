package pl.muybien.subscription;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.customer.CustomerClient;
import pl.muybien.exception.CustomerNotFoundException;
import pl.muybien.exception.OwnershipException;
import pl.muybien.exception.SubscriptionNotFoundException;
import pl.muybien.finance.FinanceServiceFactory;

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
    SubscriptionDetailDTO createIncreaseSubscription(String authHeader, SubscriptionRequest request) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not created:: No Customer exists with ID: %s"
                                .formatted(customer.id())));

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
    SubscriptionDetailDTO createDecreaseSubscription(String authHeader, SubscriptionRequest request) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not created:: No Customer exists with ID: %s"
                                .formatted(customer.id())));

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
    void deleteSubscription(String authHeader, Long subscriptionDetailId) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        var subscription = subscriptionRepository.findByCustomerId(customer.id())
                .orElseThrow(() -> new SubscriptionNotFoundException(
                        "Subscription not deleted:: No Subscription exists for customer ID: %s"
                                .formatted(customer.id())));

        var subscriptionDetail = subscriptionDetailRepository.findById(subscriptionDetailId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subscription not deleted:: No Subscription exists with ID: %d".
                                formatted(subscriptionDetailId)));

        if (!subscriptionDetail.getCustomerId().equals(customer.id())) {
            throw new OwnershipException("Subscription deletion failed:: Customer id mismatch");
        }

        var financeService = financeServiceFactory.getService(subscriptionDetail.getFinanceName());
        financeService.deleteSubscription(subscription.getId(), customer.id());

        subscriptionDetailRepository.delete(subscriptionDetail);
    }

    @Transactional(readOnly = true)
    List<SubscriptionDetailDTO> findAllSubscriptions(String authHeader) {
        var customer = customerClient.fetchCustomerFromHeader(authHeader);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        return subscriptionRepository.findByCustomerId(customer.id())
                .stream()
                .flatMap(s -> s.getSubscriptionDetails()
                        .stream()
                        .map(detailDTOMapper::mapToDTO))
                .collect(Collectors.toList());
    }
}
