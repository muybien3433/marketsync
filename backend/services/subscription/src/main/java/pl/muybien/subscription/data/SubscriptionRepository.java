package pl.muybien.subscription.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    @Query(value = "{ 'subscriptions.?0': { '$exists': true } }", fields = "{ 'subscriptions.$': 1 }")
    Optional<Subscription> findByUri(String uri);

    @Query(value = "{}", fields = "{ 'subscriptions': 1 }")
    Page<Subscription> findAllSubscriptions(Pageable pageable);

}
