package pl.muybien.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.muybien.entity.finance.Currency;

@Repository
public interface CurrencyRepository extends MongoRepository<Currency, String> {
}
