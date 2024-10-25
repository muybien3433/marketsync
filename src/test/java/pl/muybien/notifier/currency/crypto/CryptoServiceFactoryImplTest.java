package pl.muybien.notifier.currency.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import pl.muybien.notifier.handler.CryptoNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CryptoServiceFactoryImplTest {

    private CryptoServiceFactoryImpl cryptoServiceFactoryImpl;
    private Map<String, CryptoService> services;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        services = new HashMap<>(); // needs to be before cryptoServiceFactoryImpl initialization
        cryptoServiceFactoryImpl = new CryptoServiceFactoryImpl(services);
    }

    @Test
    void getServiceSuccess() {
        var cryptoService = mock(CryptoService.class);
        services.put("somecrypto", cryptoService);

        CryptoService service = cryptoServiceFactoryImpl.getService("Somecrypto");

        assertNotNull(service);
        assertSame(service, cryptoService);
    }

    @Test
    void getServiceNotFound() {
        CryptoNotFoundException e = assertThrows(CryptoNotFoundException.class, () ->
                cryptoServiceFactoryImpl.getService("Somecrypto")
        );

        assertEquals("No service found for type: Somecrypto", e.getMessage());
    }
}