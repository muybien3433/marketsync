package pl.muybien.subscription;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping("/increase")
    public ResponseEntity<SubscriptionDetailDTO> createIncreaseSubscription(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createIncreaseSubscription(authHeader, request));
    }

    @PostMapping("/decrease")
    public ResponseEntity<SubscriptionDetailDTO> createDecreaseSubscription(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createDecreaseSubscription(authHeader, request));
    }

    @DeleteMapping("/{subscription-detail-id}")
    public ResponseEntity<String> deleteSubscription(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("subscription-detail-id") Long subscriptionDetailId
    ) {
        service.deleteSubscription(authHeader, subscriptionDetailId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDetailDTO>> findAllSubscriptions(
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(service.findAllSubscriptions(authHeader));
    }
}