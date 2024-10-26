package pl.muybien.marketsync.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.asset.AssetTarget;
import pl.muybien.marketsync.handler.SubscriptionDeletionException;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionListManager {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionDTOMapper subscriptionDTOMapper;

    @Transactional
    public void addSubscriptionToList(AssetTarget assetTarget) {
        var subscription = Subscription.builder()
                .assetId(assetTarget.getId())
                .upperBoundPrice(assetTarget.getUpperBoundPrice())
                .lowerBoundPrice(assetTarget.getLowerBoundPrice())
                .assetName(assetTarget.getName())
                .customer(assetTarget.getCustomer())
                .customerEmail(assetTarget.getCustomer().getEmail())
                .createdAt(LocalDateTime.now())
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
    public List<SubscriptionDTO> findAllCustomerSubscriptions(OidcUser oidcUser) {
        return subscriptionRepository.findAllByCustomerEmail(oidcUser.getEmail())
                .stream()
                .flatMap(List::stream)
                .map(subscriptionDTOMapper::mapToDTO)
                .toList();
    }
}
