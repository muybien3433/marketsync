package pl.muybien.finance;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface FinanceRepository extends MongoRepository<Finance, String> {
    @Query("{ 'financeDetails.?0': { $exists: true } }")
    Optional<Finance> findFinanceByAssetType(String assetType);
}
