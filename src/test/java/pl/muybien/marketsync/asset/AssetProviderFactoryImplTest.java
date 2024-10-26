package pl.muybien.marketsync.asset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import pl.muybien.marketsync.handler.AssetNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AssetProviderFactoryImplTest {

    private AssetProviderFactoryImpl assetProviderFactoryImpl;
    private Map<String, AssetProvider> assertProviders;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        assertProviders = new HashMap<>();
        assetProviderFactoryImpl = new AssetProviderFactoryImpl(assertProviders);
    }

    @Test
    void getProviderSuccess() {
        var assertProvider = mock(AssetProvider.class);
        assertProviders.put("provider", assertProvider);

        AssetProvider provider = assetProviderFactoryImpl.getProvider("provider");

        assertNotNull(provider);
        assertSame(assertProvider, provider);
    }

    @Test
    void getProviderNotFound() {
        AssetNotFoundException e = assertThrows(AssetNotFoundException.class, () ->
                assetProviderFactoryImpl.getProvider("provider"));

        assertEquals("No provider found for: provider", e.getMessage());
    }
}