package pl.muybien.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import pl.muybien.customer.CustomerClient;
import pl.muybien.customer.CustomerResponse;
import pl.muybien.exception.OwnershipException;
import pl.muybien.finance.FinanceClient;
import pl.muybien.finance.FinanceResponse;
import pl.muybien.subscription.data.Subscription;
import pl.muybien.subscription.data.SubscriptionDetail;
import pl.muybien.subscription.data.SubscriptionRepository;
import pl.muybien.subscription.dto.SubscriptionDetailDTO;
import pl.muybien.subscription.dto.SubscriptionDetailDTOMapper;
import pl.muybien.subscription.request.SubscriptionDeletionRequest;
import pl.muybien.subscription.request.SubscriptionRequest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private CustomerClient customerClient;

    @Mock
    private FinanceClient financeClient;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private SubscriptionDetailDTOMapper detailDTOMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private CustomerResponse customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = CustomerResponse.builder()
                .id("customerId")
                .firstName("Joe")
                .lastName("Doe")
                .email("joe.doe@mail.com")
                .build();
    }

    @Test
    void createIncreaseSubscription_shouldAddNewSubscription() {
        String authHeader = "Bearer token";
        var request = new SubscriptionRequest("bitcoin", 3000.0, "cryptos", "USD", "email");
        var finance = new FinanceResponse("Bitcoin", "BTC", BigDecimal.valueOf(100000.0), "USD", "cryptos");
        var existingSubscription = new Subscription();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(financeClient.findFinanceWithDefaultCurrency("cryptos", "bitcoin")).thenReturn(finance);
        when(subscriptionRepository.findByUri("bitcoin")).thenReturn(Optional.of(existingSubscription));

        subscriptionService.createIncreaseSubscription(authHeader, request);

        verify(subscriptionRepository).save(existingSubscription);
        assertFalse(existingSubscription.getSubscriptions().isEmpty());
    }

    @Test
    void createDecreaseSubscription_shouldAddNewSubscription() {
        String authHeader = "Bearer token";
        var request = new SubscriptionRequest("ethereum", 3000.0, "cryptos", "USD", "email");
        var finance = new FinanceResponse("Ethereum", "BTC", BigDecimal.valueOf(100000.0), "USD", "cryptos");
        var existingSubscription = new Subscription();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(financeClient.findFinanceWithDefaultCurrency("cryptos", "ethereum")).thenReturn(finance);
        when(subscriptionRepository.findByUri("ethereum")).thenReturn(Optional.of(existingSubscription));

        subscriptionService.createDecreaseSubscription(authHeader, request);

        verify(subscriptionRepository).save(existingSubscription);
        assertFalse(existingSubscription.getSubscriptions().isEmpty());
    }

    @Test
    void deleteSubscription_shouldRemoveSubscriptionDetail() {
        String authHeader = "Bearer token";
        SubscriptionDeletionRequest request = new SubscriptionDeletionRequest("bitcoin", "detail-id-123");
        var subscriptionDetail = SubscriptionDetail.builder()
                .id("detail-id-123")
                .uri("bitcoin")
                .customerId(customer.id())
                .financeName("Bitcoin")
                .upperBoundPrice(45000.0)
                .lowerBoundPrice(null)
                .assetType("cryptos")
                .build();

        var existingSubscription = new Subscription();
        existingSubscription.getSubscriptions().put("bitcoin", new ArrayList<>(Collections.singletonList(subscriptionDetail)));

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByUri("bitcoin")).thenReturn(Optional.of(existingSubscription));

        subscriptionService.deleteSubscription(authHeader, request);

        assertTrue(existingSubscription.getSubscriptions().get("bitcoin").isEmpty());
        verify(subscriptionRepository).save(existingSubscription);
    }

    @Test
    void deleteSubscription_shouldThrowOwnershipException() {
        String authHeader = "Bearer token";
        SubscriptionDeletionRequest request = new SubscriptionDeletionRequest("bitcoin", "detail-id-123");
        var subscriptionDetail = SubscriptionDetail.builder()
                .id("detail-id-123")
                .uri("bitcoin")
                .customerId("differentCustomerId")
                .financeName("Bitcoin")
                .upperBoundPrice(45000.0)
                .lowerBoundPrice(null)
                .assetType("cryptos")
                .build();

        var existingSubscription = new Subscription();
        existingSubscription.getSubscriptions().put("bitcoin", new ArrayList<>(Collections.singletonList(subscriptionDetail)));

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByUri("bitcoin")).thenReturn(Optional.of(existingSubscription));

        assertThrows(OwnershipException.class, () -> subscriptionService.deleteSubscription(authHeader, request));
    }

    @Test
    void findAllCustomerSubscriptions_shouldReturnSubscriptions() {
        String authHeader = "Bearer token";
        var subscriptionDetail = SubscriptionDetail.builder()
                .id("detail-id-123")
                .uri("bitcoin")
                .customerId(customer.id())
                .financeName("Bitcoin")
                .upperBoundPrice(45000.0)
                .lowerBoundPrice(null)
                .assetType("cryptos")
                .build();

        var subscriptionDetailDTO = SubscriptionDetailDTO.builder()
                .id("detail-id-123")
                .customerId(customer.id())
                .financeName("Bitcoin")
                .upperBoundPrice(45000.0)
                .lowerBoundPrice(null)
                .assetType("cryptos")
                .build();

        var mockResults = mock(AggregationResults.class);
        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(mongoTemplate.aggregate(any(), eq(Subscription.class), eq(SubscriptionDetail.class))).thenReturn(mockResults);
        when(mockResults.getMappedResults()).thenReturn(List.of(subscriptionDetail));
        when(detailDTOMapper.mapToDTO(any())).thenReturn(subscriptionDetailDTO);

        var results = subscriptionService.findAllCustomerSubscriptions(authHeader);

        assertEquals(1, results.size());
        assertEquals(subscriptionDetail.getId(), results.getFirst().id());
        assertEquals(subscriptionDetail.getCustomerId(), results.getFirst().customerId());
        assertEquals(subscriptionDetail.getFinanceName(), results.getFirst().financeName());
        assertEquals(subscriptionDetail.getUpperBoundPrice(), results.getFirst().upperBoundPrice());
        assertEquals(subscriptionDetail.getLowerBoundPrice(), results.getFirst().lowerBoundPrice());
        assertEquals(subscriptionDetail.getAssetType(), results.getFirst().assetType());
    }
}
