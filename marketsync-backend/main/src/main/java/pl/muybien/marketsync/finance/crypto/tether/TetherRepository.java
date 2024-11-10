package pl.muybien.marketsync.finance.crypto.tether;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TetherRepository extends JpaRepository<Tether, Long> {
}
