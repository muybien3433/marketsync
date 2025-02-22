package pl.muybien.subscription;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.subscription.dto.SubscriptionDetailDTO;
import pl.muybien.subscription.request.SubscriptionDeletionRequest;
import pl.muybien.subscription.request.SubscriptionRequest;

import java.util.List;

@RestController
@RequestMapping("api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping("/increase")
    public ResponseEntity<String> createIncreaseSubscription(
            @RequestHeader("X-Customer-Id") String customerId,
            @RequestHeader("X-Customer-Email") String customerEmail,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        service.createIncreaseSubscription(customerId, customerEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/decrease")
    public ResponseEntity<String> createDecreaseSubscription(
            @RequestHeader("X-Customer-Id") String customerId,
            @RequestHeader("X-Customer-Email") String customerEmail,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        service.createDecreaseSubscription(customerId, customerEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteSubscription(
            @RequestHeader("X-Customer-Id") String customerId,
            @RequestBody @Valid SubscriptionDeletionRequest request
    ) {
        service.deleteSubscription(customerId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDetailDTO>> findAllSubscriptions(
            @RequestHeader("X-Customer-Id") String customerId
    ) {
        return ResponseEntity.ok(service.findAllCustomerSubscriptions(customerId));
    }
}