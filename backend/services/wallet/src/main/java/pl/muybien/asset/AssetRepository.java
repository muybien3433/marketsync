package pl.muybien.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query(
            """
                    SELECT new pl.muybien.asset.AssetHistoryDTO(
                            a.id,
                            a.name,
                            a.assetType,
                            a.count,
                            a.purchasePrice,
                            a.createdDate
                        )
                    FROM Asset a
                    WHERE a.customerId = :customerId""")
    List<AssetHistoryDTO> findAssetHistoryByCustomerId(@Param("customerId") String customerId);

    @Query(
            """
                    SELECT new pl.muybien.asset.AssetGroupDTO(
                        a.name,
                        a.uri,
                        a.assetType,
                        SUM(a.count),
                        AVG(a.purchasePrice),
                        a.currency,
                        a.customerId)
                    FROM Asset a
                    WHERE a.customerId = :customerId
                    GROUP BY a.name, a.uri, a.assetType, a.currency, a.customerId""")
    Optional<List<AssetGroupDTO>> findAndAggregateAssetsByCustomerId(@Param("customerId") String customerId);
}