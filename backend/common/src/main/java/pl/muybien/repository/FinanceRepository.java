package pl.muybien.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pl.muybien.entity.finance.Finance;
import pl.muybien.enumeration.AssetType;

import java.util.Optional;

@Repository
public interface FinanceRepository extends MongoRepository<Finance, String> {
    @Query("{ 'financeDetails.?0': { $exists: true } }")
    Optional<Finance> findFinanceByAssetType(AssetType assetType);
}
