package pl.muybien.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @GetMapping
    public ResponseEntity<CustomerResponse> fetchCustomerFromHeader(
            @RequestHeader("Authorization") String authHeader

    ) {
        return ResponseEntity.ok(service.fetchCustomerFromHeader(authHeader));
    }
}
