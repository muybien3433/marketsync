package pl.muybien.wallet.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.muybien.wallet.asset.dto.AssetGroupDTO;
import pl.muybien.entity.Asset;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

    List<Asset> findAssetHistoryByCustomerId(@Param("customerId") UUID customerId);

    @Query(
            """
                    SELECT new pl.muybien.wallet.asset.dto.AssetGroupDTO(
                        a.name,
                        a.symbol,
                        a.uri,
                        a.assetType,
                        a.unitType,
                        CAST(SUM(a.count) AS BIGDECIMAL),
                        CAST(
                            CASE\s
                                WHEN SUM(a.count) = 0 THEN 0
                                ELSE SUM(a.purchasePrice * a.count) / SUM(a.count)
                            END
                            AS BIGDECIMAL
                        ),
                        CAST(a.currentPrice AS BIGDECIMAL),
                        a.currencyType,
                        a.customerId)
                    FROM Asset a
                    WHERE a.customerId = :customerId
                    GROUP BY a.name, a.symbol, a.uri, a.assetType, a.unitType, a.currentPrice, a.currencyType, a.customerId
                   \s""")
    Optional<List<AssetGroupDTO>> findAndAggregateAssetsByCustomerId(@Param("customerId") UUID customerId);

}