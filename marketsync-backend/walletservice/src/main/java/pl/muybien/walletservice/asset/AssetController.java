package pl.muybien.walletservice.asset;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/asset")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping("{uri}")
    public ResponseEntity<String> createAsset(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable String uri,
            @RequestBody AssetRequest request
    ) {
        assetService.createOrUpdateAsset(oidcUser, uri, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("{assetId}")
    public ResponseEntity<String> deleteAsset(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long assetId
    ) {
        assetService.deleteAsset(oidcUser, assetId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
