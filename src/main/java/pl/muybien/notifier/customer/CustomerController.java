package pl.muybien.notifier.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    @GetMapping("/oauth2/authorization/google")
    public ResponseEntity<String> oauth2Callback(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok("OK");
    }
}
