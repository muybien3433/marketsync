package pl.muybien.marketsync.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.asset.AssetTarget;
import pl.muybien.marketsync.handler.SubscriptionDeletionException;
import pl.muybien.marketsync.handler.SubscriptionNotFoundException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionListManager {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void addSubscriptionToList(AssetTarget assetTarget) {
        var subscription = Subscription.builder()
                .stockId(assetTarget.getId())
                .stockName(assetTarget.getName())
                .customer(assetTarget.getCustomer())
                .customerEmail(assetTarget.getCustomer().getEmail())
                .build();

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void removeSubscriptionFromList(AssetTarget crypto) {
        try {
            subscriptionRepository.deleteByStockId(crypto.getId());
        } catch (Exception e) {
            throw new SubscriptionDeletionException(
                    "Subscription: %s id: %d could not be deleted.".
                            formatted(crypto.getName(), crypto.getId()));
        }
    }

    @Transactional(readOnly = true)
    public List<Subscription> findAllCustomerSubscriptions(OidcUser oidcUser) {
        return subscriptionRepository.findAllByCustomerEmail(oidcUser.getEmail()).
                orElseThrow(() -> new SubscriptionNotFoundException("No subscriptions found."));
    }
}
