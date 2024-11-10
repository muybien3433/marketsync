package pl.muybien.subscriptionservice.finance.crypto.ethereum;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EthereumRepository extends JpaRepository<Ethereum, Long> {
}
