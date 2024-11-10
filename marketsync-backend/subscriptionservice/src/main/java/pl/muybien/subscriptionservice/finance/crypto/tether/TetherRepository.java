package pl.muybien.subscriptionservice.finance.crypto.tether;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TetherRepository extends JpaRepository<Tether, Long> {
}
