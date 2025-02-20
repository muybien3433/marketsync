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
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        service.createIncreaseSubscription(authHeader, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/decrease")
    public ResponseEntity<String> createDecreaseSubscription(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        service.createDecreaseSubscription(authHeader, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteSubscription(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody @Valid SubscriptionDeletionRequest request
    ) {
        service.deleteSubscription(authHeader, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDetailDTO>> findAllSubscriptions(
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(service.findAllCustomerSubscriptions(authHeader));
    }
}