package pl.muybien.subscriptionservice.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionListManager subscriptionListManager;

    @PostMapping("/increase/{uri}")
    public ResponseEntity<String> createIncreaseSubscription(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable String uri,
            @RequestBody BigDecimal value
    ) {
        subscriptionService.createIncreaseSubscription(oidcUser, uri, value);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/decrease/{uri}")
    public ResponseEntity<String> createDecreaseSubscription(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable String uri,
            @RequestBody BigDecimal value
    ) {
        subscriptionService.createDecreaseSubscription(oidcUser, uri, value);
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    public ResponseEntity<List<SubscriptionDetailDTO>> findAllSubscriptions(
            @AuthenticationPrincipal OidcUser oidcUser) {
        List<SubscriptionDetailDTO> subscriptions = subscriptionListManager.findAllCustomerSubscriptions(oidcUser.getEmail());
        return ResponseEntity.ok(subscriptions);
    }
}