package pl.muybien.finance.currency;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String> {
    Optional<Currency> findCurrencyByName(String name);
}
