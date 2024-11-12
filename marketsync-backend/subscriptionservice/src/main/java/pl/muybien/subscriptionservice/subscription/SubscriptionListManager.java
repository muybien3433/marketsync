package pl.muybien.subscriptionservice.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.subscriptionservice.finance.FinanceTarget;
import pl.muybien.subscriptionservice.handler.SubscriptionDeletionException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubscriptionListManager {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionDTOMapper subscriptionDTOMapper;

    @Transactional
    public void addSubscriptionToList(FinanceTarget financeTarget) {
        var subscriptionDetail = SubscriptionDetail.builder()
                .financeId(financeTarget.getId())
                .upperBoundPrice(financeTarget.getUpperBoundPrice())
                .lowerBoundPrice(financeTarget.getLowerBoundPrice())
                .name(financeTarget.getName())
                .customerEmail(financeTarget.getCustomerEmail())
                .createdAt(LocalDateTime.now())
                .build();

        Subscription subscription = Subscription.builder()
                .subscriptions(List.of(subscriptionDetail))
                .build();

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void removeSubscriptionFromList(FinanceTarget crypto) {
        try {
            subscriptionRepository.deleteByFinanceId(crypto.getId());
        } catch (Exception e) {
            throw new SubscriptionDeletionException(
                    "Subscription: %s id: %d could not be deleted.".
                            formatted(crypto.getName(), crypto.getId()));
        }
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDTO> findAllCustomerSubscriptions(String email) {
        return subscriptionRepository.findAllByCustomerEmail(email)
                .stream()
                .flatMap(s -> s.getSubscriptions()
                        .stream()
                        .map(subscriptionDTOMapper::mapToDTO))
                .sorted((s1, s2) -> s2.createdAt().compareTo(s1.createdAt()))
                .collect(Collectors.toList());
    }
}
