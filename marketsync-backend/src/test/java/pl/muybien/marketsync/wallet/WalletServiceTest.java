package pl.muybien.marketsync.wallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @InjectMocks
    WalletService walletService;

    @Mock
    WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveNewWallet() {
        var wallet = mock(Wallet.class);

        walletService.saveNewWallet(wallet);

        verify(walletRepository, times(1)).save(wallet);
    }
}