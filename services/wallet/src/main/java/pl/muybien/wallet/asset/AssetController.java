package pl.muybien.wallet.asset;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/wallets/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService service;

    @PostMapping
    public ResponseEntity<String> createAsset(
            @RequestBody @Valid AssetRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        service.createAsset(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAsset(
            @RequestBody @Valid AssetDeletionRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        service.deleteAsset(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}