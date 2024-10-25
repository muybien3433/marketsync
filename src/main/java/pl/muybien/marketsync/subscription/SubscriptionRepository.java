package pl.muybien.marketsync.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Subscription s WHERE s.stockId = :stockId")
    void deleteByStockId(@Param("stockId") Long stockId);

    @Query("SELECT s FROM Subscription s WHERE s.customerEmail = :email")
    Optional<List<Subscription>> findAllByCustomerEmail(@Param("email") String email);
}
