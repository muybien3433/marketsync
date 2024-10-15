package pl.muybien.notifier.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    protected void addSubscription(Long id) {
        var subscription = findSubscriptionById(id);
        subscriptionRepository.save(subscription);
    }

    @Transactional
    protected void removeSubscription(Long id) {
        var subscription = findSubscriptionById(id);
        subscriptionRepository.delete(subscription);
    }

    private Subscription findSubscriptionById(Long id) {
        return subscriptionRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Subscription not found"));
    }
}
