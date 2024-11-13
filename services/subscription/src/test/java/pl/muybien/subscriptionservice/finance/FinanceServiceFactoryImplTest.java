package pl.muybien.subscriptionservice.finance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import pl.muybien.subscriptionservice.handler.FinanceNotFoundException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class FinanceServiceFactoryImplTest {

    private FinanceServiceFactoryImpl financeServiceFactoryImpl;
    private Map<String, FinanceService> services;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        services = new HashMap<>();
        financeServiceFactoryImpl = new FinanceServiceFactoryImpl(services);
    }

    @Test
    void getServiceSuccess() {
        var cryptoService = mock(FinanceService.class);
        services.put("somecrypto", cryptoService);

        FinanceService service = financeServiceFactoryImpl.getService("Somecrypto");

        assertNotNull(service);
        assertSame(service, cryptoService);
    }

    @Test
    void getServiceNotFound() {
        FinanceNotFoundException e = assertThrows(FinanceNotFoundException.class, () ->
                financeServiceFactoryImpl.getService("Somecrypto")
        );

        assertEquals("No service found for type: Somecrypto", e.getMessage());
    }
}