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
            @RequestBody @Valid SubscriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createDecreaseSubscription(request));
    }

    @PostMapping("/decrease")
    public ResponseEntity<SubscriptionDetailDTO> createDecreaseSubscription(
            @RequestBody @Valid SubscriptionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createDecreaseSubscription(request));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteSubscription(
            @RequestBody @Valid SubscriptionDeletionRequest request
    ) {
        service.deleteSubscription(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customer-id}")
    public ResponseEntity<List<SubscriptionDetailDTO>> findAllSubscriptions(
            @PathVariable("customer-id") Long customerId
    ) {
        return ResponseEntity.ok(service.findAllSubscriptions(customerId));
    }
}