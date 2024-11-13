package pl.muybien.walletservice.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public void saveNewWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }
}