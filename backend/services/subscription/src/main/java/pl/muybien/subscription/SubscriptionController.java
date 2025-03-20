package pl.muybien.subscription;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.subscription.dto.SubscriptionDetailDTO;

import java.util.List;

@RestController
@RequestMapping("api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping
    public ResponseEntity<String> createSubscription(
            @RequestHeader("X-Customer-Id") String customerId,
            @RequestHeader("X-Customer-Email") String customerEmail,
            @RequestHeader(value = "X-Customer-Number", required = false) String customerNumber,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        service.createSubscription(customerId, customerEmail, customerNumber, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteSubscription(
            @RequestHeader("X-Customer-Id") String customerId,
            @RequestParam String uri,
            @RequestParam String id
    ) {
        service.deleteSubscription(customerId, uri, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDetailDTO>> findAllSubscriptions(
            @RequestHeader("X-Customer-Id") String customerId
    ) {
        return ResponseEntity.ok(service.findAllCustomerSubscriptions(customerId));
    }
}