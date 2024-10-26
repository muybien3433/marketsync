package pl.muybien.marketsync.asset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import pl.muybien.marketsync.handler.AssetNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AssetServiceFactoryImplTest {

    private AssetServiceFactoryImpl assetServiceFactoryImpl;
    private Map<String, AssetService> services;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        services = new HashMap<>();
        assetServiceFactoryImpl = new AssetServiceFactoryImpl(services);
    }

    @Test
    void getServiceSuccess() {
        var cryptoService = mock(AssetService.class);
        services.put("somecrypto", cryptoService);

        AssetService service = assetServiceFactoryImpl.getService("Somecrypto");

        assertNotNull(service);
        assertSame(service, cryptoService);
    }

    @Test
    void getServiceNotFound() {
        AssetNotFoundException e = assertThrows(AssetNotFoundException.class, () ->
                assetServiceFactoryImpl.getService("Somecrypto")
        );

        assertEquals("No service found for type: Somecrypto", e.getMessage());
    }
}