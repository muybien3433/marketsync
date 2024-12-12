package pl.muybien.wallet.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.wallet.asset.AssetDTO;

import java.util.List;

@RestController
@RequestMapping("api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    @GetMapping
    public ResponseEntity<List<AssetDTO>> displayOrCreateWallet(
            @RequestHeader("Authorization") String authHeader
    ) {
        return ResponseEntity.ok(service.displayOrCreateWallet(authHeader));
    }
}
