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
                            a.symbol,
                            a.count,
                            a.currency,
                            a.purchasePrice,
                            a.createdDate,
                            a.assetType
                        )
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
                        SUM(a.count),
                        AVG(a.purchasePrice),
                        a.currency,
                        a.customerId)
                    FROM Asset a
                    WHERE a.customerId = :customerId
                    GROUP BY a.name, a.symbol, a.uri, a.assetType, a.currency, a.customerId""")
    Optional<List<AssetGroupDTO>> findAndAggregateAssetsByCustomerId(@Param("customerId") String customerId);
}