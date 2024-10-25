package pl.muybien.marketsync.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.currency.CurrencyTarget;
import pl.muybien.marketsync.handler.SubscriptionDeletionException;
import pl.muybien.marketsync.handler.SubscriptionNotFoundException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionListManager {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void addSubscriptionToList(CurrencyTarget currencyTarget) {
        var subscription = Subscription.builder()
                .stockId(currencyTarget.getId())
                .stockName(currencyTarget.getName())
                .customer(currencyTarget.getCustomer())
                .customerEmail(currencyTarget.getCustomer().getEmail())
                .build();

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void removeSubscriptionFromList(CurrencyTarget crypto) {
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
