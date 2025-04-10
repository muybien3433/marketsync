package pl.muybien.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.muybien.asset.dto.AssetGroupDTO;
import pl.muybien.asset.dto.AssetHistoryDTO;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query(
            """
                    SELECT new pl.muybien.asset.dto.AssetHistoryDTO(
                            a.id,
                            a.name,
                            a.uri,
                            a.symbol,
                            a.count,
                            a.currencyType,
                            a.purchasePrice,
                            a.currentPrice,
                            a.createdDate,
                            a.assetType,
                            a.unitType,
                            a.comment)
                    FROM Asset a
                    WHERE a.customerId = :customerId""")
    List<AssetHistoryDTO> findAssetHistoryByCustomerId(@Param("customerId") String customerId);

    @Query(
            """
                    SELECT new pl.muybien.asset.dto.AssetGroupDTO(
                        a.name,
                        a.symbol,
                        a.uri,
                        a.assetType,
                        a.unitType,
                        CAST(SUM(a.count) AS BIGDECIMAL),
                        CAST(SUM(a.purchasePrice * a.count) / SUM(a.count) AS BIGDECIMAL),
                        CAST(a.currentPrice AS BIGDECIMAL),
                        a.currencyType,
                        a.customerId)
                    FROM Asset a
                    WHERE a.customerId = :customerId
                    GROUP BY a.name, a.symbol, a.uri, a.assetType, a.unitType, a.currentPrice, a.currencyType, a.customerId""")
    Optional<List<AssetGroupDTO>> findAndAggregateAssetsByCustomerId(@Param("customerId") String customerId);
}