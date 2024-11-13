package pl.muybien.subscriptionservice.finance.crypto.bitcoin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BitcoinRepository extends JpaRepository<Bitcoin, Long> {
}
