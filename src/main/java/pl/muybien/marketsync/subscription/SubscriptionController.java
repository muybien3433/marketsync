package pl.muybien.marketsync.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionListManager subscriptionListManager;

    @PostMapping("/{uri}")
    public ResponseEntity<String> createSubscription(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable String uri,
            @RequestBody SubscriptionRequest request
    ) {
        subscriptionService.addSubscription(oidcUser, uri,
                request.upperValueInPercent(), request.lowerValueInPercent());
        return ResponseEntity.ok("Subscription created.");
    }

    @DeleteMapping("/{uri}/{id}")
    public ResponseEntity<String> deleteSubscription(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable String uri,
            @PathVariable Long id) {
        subscriptionService.removeSubscription(oidcUser, uri, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Subscription> findAllSubscriptions(
            @AuthenticationPrincipal OidcUser oidcUser) {
        return subscriptionListManager.findAllCustomerSubscriptions(oidcUser);
    }
}
