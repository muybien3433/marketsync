package pl.muybien.wallet.wallet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.muybien.wallet.asset.AssetDTO;

import java.util.List;

@RestController
@RequestMapping("api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService service;

    @DeleteMapping
    public ResponseEntity<String> deleteWallet(
            @RequestBody @Valid WalletRequest request
    ) {
        service.deleteWallet(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<AssetDTO>> displayOrCreateWallet(
            @RequestBody @Valid WalletRequest request
    ) {
        return ResponseEntity.ok(service.displayOrCreateWallet(request));
    }
}
