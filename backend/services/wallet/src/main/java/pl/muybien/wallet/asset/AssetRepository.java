package pl.muybien.wallet.asset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    @Query(
            """
                    SELECT new pl.muybien.wallet.asset.AssetHistoryDTO(
                            a.id,
                            a.name,
                            a.type,
                            a.count,
                            a.purchasePrice,
                            a.createdDate
                        )
                    FROM Asset a
                    WHERE a.customerId = :customerId""")
    List<AssetHistoryDTO> findAssetHistoryByCustomerId(@Param("customerId") String customerId);

    @Query(
            """
                    SELECT new pl.muybien.wallet.asset.AssetGroupDTO(
                        a.id,
                        a.name,
                        a.uri,
                        a.type,
                        SUM(a.count),
                        AVG(a.purchasePrice),
                        a.currency,
                        a.customerId)
                    FROM Asset a
                    WHERE a.customerId = :customerId
                    GROUP BY a.name, a.currency, a.uri, a.customerId, a.id""")
    List<AssetGroupDTO> findAndAggregateAssetsByCustomerId(@Param("customerId") String customerId);
}