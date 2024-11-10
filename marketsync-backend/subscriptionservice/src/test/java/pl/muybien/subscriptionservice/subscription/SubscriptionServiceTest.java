//package pl.muybien.subscriptionservice.subscription;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import pl.muybien.subscriptionservice.finance.FinanceProvider;
//import pl.muybien.subscriptionservice.finance.FinanceProviderFactory;
//import pl.muybien.subscriptionservice.finance.FinanceService;
//import pl.muybien.subscriptionservice.finance.FinanceServiceFactory;
//import pl.muybien.subscriptionservice.finance.crypto.Crypto;
//import pl.muybien.subscriptionservice.handler.InvalidSubscriptionParametersException;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//class SubscriptionServiceTest {
//
//    @InjectMocks
//    private SubscriptionService subscriptionService;
//
//    @Mock
//    private FinanceServiceFactory financeServiceFactory;
//
//    @Mock
//    private FinanceProviderFactory financeProviderFactory;
//
//    @Mock
//    private FinanceService financeService;
//
//    @Mock
//    private FinanceProvider financeProvider;
//
//    @Mock
//    private OidcUser oidcUser;
//
//    private final String uri = "cryptoUri";
//    private final String provider = "crypto";
//    private final String email = "test@example.com";
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void addIncreaseSubscription() {
//        String assetName = "test-asset";
//        BigDecimal value = BigDecimal.valueOf(10.0);
//        var currentCrypto = Mockito.mock(Crypto.class);
//
//        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
//        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
//        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
//        when(currentCrypto.getName()).thenReturn(assetName);
//        when(oidcUser.getEmail()).thenReturn(email);
//
//        subscriptionService.addIncreaseSubscription(oidcUser, uri, value);
//
//        verify(financeServiceFactory).getService(uri);
//        verify(financeProvider).fetchFinance(uri);
//        verify(financeService).createAndSaveSubscription(email, assetName, value, null);
//    }
//
//    @Test
//    void addDecreaseSubscription() {
//        String assetName = "test-asset";
//        BigDecimal value = BigDecimal.valueOf(10.0);
//        var currentCrypto = Mockito.mock(Crypto.class);
//
//        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
//        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
//        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
//        when(currentCrypto.getName()).thenReturn(assetName);
//        when(oidcUser.getEmail()).thenReturn(email);
//
//        subscriptionService.addDecreaseSubscription(oidcUser, uri, value);
//
//        verify(financeServiceFactory).getService(uri);
//        verify(financeProvider).fetchFinance(uri);
//        verify(financeService).createAndSaveSubscription(email, assetName, null, value);
//    }
//
//    @Test
//    void addSubscriptionValueNull() {
//        String assetName = "test-asset";
//        BigDecimal currentPrice = BigDecimal.valueOf(1000);
//        var currentCrypto = mock(Crypto.class);
//
//        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
//        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
//        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
//        when(currentCrypto.getName()).thenReturn(assetName);
//        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
//        when(oidcUser.getEmail()).thenReturn(email);
//
//        InvalidSubscriptionParametersException e = assertThrows(InvalidSubscriptionParametersException.class, () ->
//                subscriptionService.addIncreaseSubscription(oidcUser, uri, null));
//
//        assertEquals("Value is required and must be grater than zero.", e.getMessage());
//    }
//
//    @Test
//    void addSubscriptionValueZero() {
//        String assetName = "test-asset";
//        BigDecimal currentPrice = BigDecimal.valueOf(1000);
//        var currentCrypto = mock(Crypto.class);
//
//        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
//        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
//        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
//        when(currentCrypto.getName()).thenReturn(assetName);
//        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
//        when(oidcUser.getEmail()).thenReturn(email);
//
//        subscriptionService.addDecreaseSubscription(oidcUser, uri, BigDecimal.ZERO);
//
//        InvalidSubscriptionParametersException e = assertThrows(InvalidSubscriptionParametersException.class, () ->
//                subscriptionService.addIncreaseSubscription(oidcUser, uri, null));
//
//        assertEquals("Value is required and must be grater than zero.", e.getMessage());
//    }
//
//    @Test
//    void addSubscriptionValueNegative() {
//        String assetName = "test-asset";
//        BigDecimal currentPrice = BigDecimal.valueOf(1000);
//        var currentCrypto = mock(Crypto.class);
//
//        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
//        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
//        when(financeProvider.fetchFinance(uri)).thenReturn(currentCrypto);
//        when(currentCrypto.getName()).thenReturn(assetName);
//        when(currentCrypto.getPriceUsd()).thenReturn(currentPrice);
//        when(oidcUser.getEmail()).thenReturn(email);
//
//        subscriptionService.addDecreaseSubscription(oidcUser, uri, BigDecimal.valueOf(-100));
//
//        InvalidSubscriptionParametersException e = assertThrows(InvalidSubscriptionParametersException.class, () ->
//                subscriptionService.addIncreaseSubscription(oidcUser, uri, null));
//
//        assertEquals("Value is required and must be grater than zero.", e.getMessage());
//    }
//
//    @Test
//    void removeSubscription() {
//        Long subscriptionId = 1L;
//
//        when(financeProviderFactory.getProvider(provider)).thenReturn(financeProvider);
//        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
//
//        subscriptionService.removeSubscription(oidcUser, uri, subscriptionId);
//
//        verify(financeServiceFactory).getService(uri);
//        verify(financeService, times(1)).removeSubscription(oidcUser, subscriptionId);
//    }
//}