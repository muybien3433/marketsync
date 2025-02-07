package pl.muybien.finance;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FinanceRepository extends MongoRepository<Finance, String> {
    Optional<Finance> findFinanceByAssetTypeIgnoreCase(String assetType);
}
