package pl.muybien.marketsync.currency.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import pl.muybien.marketsync.currency.CurrencyService;
import pl.muybien.marketsync.currency.CurrencyServiceFactoryImpl;
import pl.muybien.marketsync.handler.CurrencyNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CurrencyServiceFactoryImplTest {

    private CurrencyServiceFactoryImpl currencyServiceFactoryImpl;
    private Map<String, CurrencyService> services;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        services = new HashMap<>(); // needs to be before cryptoServiceFactoryImpl initialization
        currencyServiceFactoryImpl = new CurrencyServiceFactoryImpl(services);
    }

    @Test
    void getServiceSuccess() {
        var cryptoService = mock(CurrencyService.class);
        services.put("somecrypto", cryptoService);

        CurrencyService service = currencyServiceFactoryImpl.getService("Somecrypto");

        assertNotNull(service);
        assertSame(service, cryptoService);
    }

    @Test
    void getServiceNotFound() {
        CurrencyNotFoundException e = assertThrows(CurrencyNotFoundException.class, () ->
                currencyServiceFactoryImpl.getService("Somecrypto")
        );

        assertEquals("No service found for type: Somecrypto", e.getMessage());
    }
}