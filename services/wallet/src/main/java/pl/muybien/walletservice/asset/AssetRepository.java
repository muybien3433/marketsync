package pl.muybien.walletservice.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query("SELECT a FROM Asset a WHERE a.wallet.id = :walletId")
    @Transactional(readOnly = true)
    List<Asset> findAllAssetsByWalletId(Long walletId);

    @Query("""
            SELECT a FROM Asset a WHERE a.wallet.id = :walletId
            AND a.name = :assetName
            """)
    Optional<Asset> findAssetByWalletIdAndAssetName(Long walletId, String assetName);
}
