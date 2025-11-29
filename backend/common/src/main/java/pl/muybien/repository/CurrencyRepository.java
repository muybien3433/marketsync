package pl.muybien.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.muybien.entity.Currency;

import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String> {
    Optional<Currency> findCurrencyByName(String name);
}
