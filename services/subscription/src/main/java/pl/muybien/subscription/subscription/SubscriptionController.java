package pl.muybien.subscription.subscription;

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
            @RequestBody @Valid SubscriptionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createIncreaseSubscription(request, authorizationHeader));
    }

    @PostMapping("/decrease")
    public ResponseEntity<SubscriptionDetailDTO> createDecreaseSubscription(
            @RequestBody @Valid SubscriptionRequest request,
            @RequestHeader("Authorization") String authorizationHeader

    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createDecreaseSubscription(request, authorizationHeader));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteSubscription(
            @RequestBody @Valid SubscriptionDeletionRequest request,
            @RequestHeader("Authorization") String authorizationHeader

    ) {
        service.deleteSubscription(request, authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<List<SubscriptionDetailDTO>> findAllSubscriptions(
            @PathVariable("customer-id") Long customerId
    ) {
        return ResponseEntity.ok(service.findAllSubscriptions(customerId));
    }
}