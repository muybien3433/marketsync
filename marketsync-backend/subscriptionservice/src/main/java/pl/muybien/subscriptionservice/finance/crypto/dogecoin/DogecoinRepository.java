package pl.muybien.subscriptionservice.finance.crypto.dogecoin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DogecoinRepository extends JpaRepository<Dogecoin, Long> {
}