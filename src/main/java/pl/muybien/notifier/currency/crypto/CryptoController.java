package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/subscribe")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoSubscriptionManager cryptoSubscriptionManager;

    @PostMapping
    public ResponseEntity<String> createSubscription(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody CryptoRequest request
    ) {
        cryptoSubscriptionManager.addSubscription(oidcUser, request.uri(),
                request.upperValueInPercent(), request.lowerValueInPercent());
        return ResponseEntity.ok("Subscription created.");
    }

    @DeleteMapping("/{uri}/{id}")
    public ResponseEntity<String> deleteSubscription(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable String uri,
            @PathVariable Long id) {
        cryptoSubscriptionManager.removeSubscription(oidcUser, uri, id);
        return ResponseEntity.noContent().build();
    }

    
}
