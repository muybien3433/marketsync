package pl.muybien.marketsync.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.muybien.marketsync.asset.AssetDTO;
import pl.muybien.marketsync.asset.AssetService;

import java.util.List;

@RestController
@RequestMapping("api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final AssetService assetService;
    private final WalletService walletService;

    @GetMapping
    ResponseEntity<List<AssetDTO>> findAllWalletAssets(
            @AuthenticationPrincipal OidcUser oidcUser
    ) {
        var assets = assetService.findAllWalletAssets(oidcUser);
        return ResponseEntity.ok(assets);
    }
}
