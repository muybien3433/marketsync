package pl.muybien.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.muybien.entity.Currency;

public interface CurrencyRepository extends MongoRepository<Currency, String> {
}
