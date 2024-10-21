package pl.muybien.notifier.currency.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequiredArgsConstructor
public class CryptoController {

    private final CryptoService cryptoService;

    @PostMapping("api/v1/subscribe")
    public ResponseEntity<String> createSubscription(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CryptoRequest request
    ) {
        cryptoService.addSubscription(jwt, request.uri(),
                request.upperValueInPercent(), request.lowerValueInPercent());
        return ResponseEntity.ok("OK");
    }
}
