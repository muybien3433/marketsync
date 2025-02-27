package pl.muybien.subscription;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createIncreaseSubscription_shouldAddNewSubscription() {
        String customerId = "customerId";
        String customerEmail = "joe.doe@mail.com";
        var request = new SubscriptionRequest(
                "bitcoin", 3000.0, AssetType.CRYPTOS.name(), CurrencyType.USD.name(), NotificationType.EMAIL.name());
        var finance = new FinanceResponse(
                "Bitcoin", "BTC", "bitcoin", BigDecimal.valueOf(100000.0), CurrencyType.USD.name(), AssetType.CRYPTOS.name());
        var existingSubscription = new Subscription();

        when(financeClient.findFinanceByTypeAndUri(AssetType.CRYPTOS.name(), "bitcoin")).thenReturn(finance);
        when(subscriptionRepository.findByUri("bitcoin")).thenReturn(Optional.of(existingSubscription));

        subscriptionService.createIncreaseSubscription(customerId, customerEmail, request);

        verify(subscriptionRepository).save(existingSubscription);
        assertFalse(existingSubscription.getSubscriptions().isEmpty());
    }

    @Test
    void createDecreaseSubscription_shouldAddNewSubscription() {
        String customerId = "customerId";
        String customerEmail = "joe.doe@mail.com";
        var request = new SubscriptionRequest(
                "ethereum", 3000.0, AssetType.CRYPTOS.name(), CurrencyType.USD.name(), NotificationType.EMAIL.name());
        var finance = new FinanceResponse(
                "Ethereum", "ETH", "ethereum", BigDecimal.valueOf(100000.0), CurrencyType.USD.name(), AssetType.CRYPTOS.name());
        var existingSubscription = new Subscription();

        when(financeClient.findFinanceByTypeAndUri(AssetType.CRYPTOS.name(), "ethereum")).thenReturn(finance);
        when(subscriptionRepository.findByUri("ethereum")).thenReturn(Optional.of(existingSubscription));

        subscriptionService.createDecreaseSubscription(customerId, customerEmail, request);

        verify(subscriptionRepository).save(existingSubscription);
        assertFalse(existingSubscription.getSubscriptions().isEmpty());
    }

    @Test
    void deleteSubscription_shouldRemoveSubscriptionDetail() {
        String customerId = "customerId";
        SubscriptionDeletionRequest request = new SubscriptionDeletionRequest("bitcoin", "detail-id-123");
        var subscriptionDetail = new SubscriptionDetail(
                "detail-id-123",
                "bitcoin",
                "customerId",
                "customerEmail",
                "Bitcoin",
                CurrencyType.USD.name(),
                45000.0,
                null,
                AssetType.CRYPTOS.name(),
                NotificationType.EMAIL.name(),
                LocalDateTime.now()
        );
        var existingSubscription = new Subscription();
        existingSubscription.getSubscriptions().put("bitcoin", new ArrayList<>(Collections.singletonList(subscriptionDetail)));

        when(subscriptionRepository.findByUri("bitcoin")).thenReturn(Optional.of(existingSubscription));

        subscriptionService.deleteSubscription(customerId, request);

        assertTrue(existingSubscription.getSubscriptions().get("bitcoin").isEmpty());
        verify(subscriptionRepository).save(existingSubscription);
    }

    @Test
    void deleteSubscription_shouldThrowOwnershipException() {
        String customerId = "wrongId";
        SubscriptionDeletionRequest request = new SubscriptionDeletionRequest("bitcoin", "detail-id-123");
        var subscriptionDetail = new SubscriptionDetail(
                "detail-id-123",
                "bitcoin",
                "differentCustomerId",
                "customerEmail",
                "Bitcoin",
                CurrencyType.USD.name(),
                45000.0,
                null,
                AssetType.CRYPTOS.name(),
                NotificationType.EMAIL.name(),
                LocalDateTime.now()
        );

        var existingSubscription = new Subscription();
        existingSubscription.getSubscriptions().put("bitcoin", new ArrayList<>(Collections.singletonList(subscriptionDetail)));

        when(subscriptionRepository.findByUri("bitcoin")).thenReturn(Optional.of(existingSubscription));

        assertThrows(OwnershipException.class, () -> subscriptionService.deleteSubscription(customerId, request));
    }

    @Test
    void findAllCustomerSubscriptions_shouldReturnSubscriptions() {
        String customerId = "customerId";
        var subscriptionDetail = new SubscriptionDetail(
                "detail-id-123",
                "bitcoin",
                customerId,
                "customerEmail",
                "Bitcoin",
                CurrencyType.USD.name(),
                45000.0,
                null,
                AssetType.CRYPTOS.name(),
                NotificationType.EMAIL.name(),
                LocalDateTime.now()
        );

        var subscriptionDetailDTO = new SubscriptionDetailDTO(
                "detail-id-123",
                "customerId",
                "Bitcoin",
                45000.0,
                null,
                AssetType.CRYPTOS.name(),
                LocalDateTime.now()
        );

        var mockResults = mock(AggregationResults.class);

        when(mongoTemplate.aggregate(any(), eq(Subscription.class), eq(SubscriptionDetail.class))).thenReturn(mockResults);
        when(mockResults.getMappedResults()).thenReturn(List.of(subscriptionDetail));
        when(detailDTOMapper.toDTO(any())).thenReturn(subscriptionDetailDTO);

        var results = subscriptionService.findAllCustomerSubscriptions(customerId);

        assertEquals(1, results.size());
        assertEquals(subscriptionDetail.id(), results.getFirst().id());
        assertEquals(subscriptionDetail.customerId(), results.getFirst().customerId());
        assertEquals(subscriptionDetail.financeName(), results.getFirst().financeName());
        assertEquals(subscriptionDetail.upperBoundPrice(), results.getFirst().upperBoundPrice());
        assertEquals(subscriptionDetail.lowerBoundPrice(), results.getFirst().lowerBoundPrice());
        assertEquals(subscriptionDetail.assetType(), results.getFirst().assetType());
    }
}
