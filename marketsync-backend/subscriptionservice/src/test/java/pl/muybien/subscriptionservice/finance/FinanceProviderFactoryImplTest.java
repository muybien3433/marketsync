package pl.muybien.subscriptionservice.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import pl.muybien.subscriptionservice.handler.FinanceNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FinanceProviderFactoryImplTest {

    private FinanceProviderFactoryImpl financeProviderFactoryImpl;
    private Map<String, FinanceProvider> assertProviders;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assertProviders = new HashMap<>();
        financeProviderFactoryImpl = new FinanceProviderFactoryImpl(assertProviders);
    }

    @Test
    void getProviderSuccess() {
        var assertProvider = mock(FinanceProvider.class);
        assertProviders.put("provider", assertProvider);

        FinanceProvider provider = financeProviderFactoryImpl.getProvider("provider");

        assertNotNull(provider);
        assertSame(assertProvider, provider);
    }

    @Test
    void getProviderNotFound() {
        FinanceNotFoundException e = assertThrows(FinanceNotFoundException.class, () ->
                financeProviderFactoryImpl.getProvider("provider"));

        assertEquals("No provider found for: provider", e.getMessage());
    }
}