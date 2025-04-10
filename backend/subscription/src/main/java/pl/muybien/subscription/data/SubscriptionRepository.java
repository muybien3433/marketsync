package pl.muybien.subscription.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface SubscriptionRepository extends MongoRepository<Subscription, String> {

    Optional<Subscription> findByUri(String uri);

    @Query(value = "{ 'subscriptionDetails.id': ?0 }", fields = "{ 'subscriptionDetails.$': 1 }")
    Optional<Subscription> findByDetailId(String detailId);

    @Query(value = "{}", fields = "{ 'subscriptionDetails': 1 }")
    Page<Subscription> findAllSubscriptions(Pageable pageable);
}