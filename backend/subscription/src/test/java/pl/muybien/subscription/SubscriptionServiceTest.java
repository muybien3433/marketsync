package pl.muybien.subscription;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.NotificationType;
import pl.muybien.enumeration.UnitType;
import pl.muybien.exception.*;
import pl.muybien.feign.FinanceClient;
import pl.muybien.response.FinanceResponse;
import pl.muybien.entity.Subscription;
import pl.muybien.entity.helper.SubscriptionDetail;
import pl.muybien.subscription.data.SubscriptionRepository;
import pl.muybien.subscription.dto.SubscriptionDetailDTO;
import pl.muybien.subscription.dto.SubscriptionDetailDTOMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private FinanceClient financeClient;

    @Mock
    private SubscriptionDetailDTOMapper detailDTOMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private final String customerId = "customer-123";
    private final String customerEmail = "test@example.com";
    private final String phoneNumber = "+1234567890";
    private final String uri = "http://example.com/asset";
    private final AssetType assetType = AssetType.STOCK;
    private final String financeName = "Example Corp";
    private final String price = "100.50";

    @Test
    void createSubscription_ThrowsWhenBothBoundsNull() {
        SubscriptionRequest request = new SubscriptionRequest(
                uri, null, null, assetType, NotificationType.EMAIL, CurrencyType.USD
        );

        assertThrows(InvalidSubscriptionParametersException.class,
                () -> subscriptionService.createSubscription(
                        customerId, customerEmail, phoneNumber, request));
    }

    @Test
    void createSubscription_ThrowsWhenBothBoundsProvided() {
        SubscriptionRequest request = new SubscriptionRequest(
                uri, 10.0, 5.0, assetType, NotificationType.EMAIL, CurrencyType.USD
        );

        assertThrows(InvalidSubscriptionParametersException.class,
                () -> subscriptionService.createSubscription(
                        customerId, customerEmail, phoneNumber, request));
    }

    @Test
    void createSubscription_ValidUpperBound_CreatesSubscription() {
        SubscriptionRequest request = new SubscriptionRequest(
                uri, 10.0, null, assetType, NotificationType.EMAIL, CurrencyType.USD
        );

        FinanceResponse financeResponse = new FinanceResponse(
                financeName, "SYM", uri, UnitType.UNIT, price, CurrencyType.USD, assetType, LocalDateTime.now()
        );

        when(financeClient.findFinanceByTypeAndUri(assetType, uri))
                .thenReturn(financeResponse);
        when(subscriptionRepository.findByUri(uri)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> subscriptionService.createSubscription(
                customerId, customerEmail, phoneNumber, request));

        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void createSubscription_EmailNotificationWithoutEmail_Throws() {
        SubscriptionRequest request = new SubscriptionRequest(
                uri, null, 5.0, assetType, NotificationType.EMAIL, CurrencyType.USD
        );

        assertThrows(InvalidSubscriptionParametersException.class,
                () -> subscriptionService.createSubscription(
                        customerId, null, phoneNumber, request));
    }

    @Test
    void createSubscription_SmsNotificationWithoutPhone_Throws() {
        SubscriptionRequest request = new SubscriptionRequest(
                uri, 10.0, null, assetType, NotificationType.SMS, CurrencyType.USD
        );

        assertThrows(InvalidSubscriptionParametersException.class,
                () -> subscriptionService.createSubscription(
                        customerId, customerEmail, null, request));
    }

    @Test
    void deleteSubscription_SubscriptionNotFound_Throws() {
        when(subscriptionRepository.findByUri(uri)).thenReturn(Optional.empty());

        assertThrows(SubscriptionNotFoundException.class,
                () -> subscriptionService.deleteSubscription(customerId, uri, "detail-123"));
    }

    @Test
    void deleteSubscription_DetailNotFound_Throws() {
        Subscription subscription = new Subscription();
        subscription.setUri(uri);
        when(subscriptionRepository.findByUri(uri)).thenReturn(Optional.of(subscription));

        assertThrows(SubscriptionNotFoundException.class,
                () -> subscriptionService.deleteSubscription(customerId, uri, "invalid-id"));
    }

    @Test
    void deleteSubscription_CustomerIdMismatch_Throws() {
        Subscription subscription = new Subscription();
        subscription.setUri(uri);
        SubscriptionDetail detail = new SubscriptionDetail(
                "detail-123", uri, "other-customer", "target",
                financeName, CurrencyType.USD, 10.0, null,
                AssetType.STOCK, NotificationType.EMAIL, LocalDateTime.now()
        );
        subscription.getSubscriptionDetails().add(detail);
        when(subscriptionRepository.findByUri(uri)).thenReturn(Optional.of(subscription));

        assertThrows(OwnershipException.class,
                () -> subscriptionService.deleteSubscription(customerId, uri, "detail-123"));
    }

    @Test
    void deleteSubscription_ValidRequest_RemovesDetail() {
        Subscription subscription = new Subscription();
        subscription.setUri(uri);
        SubscriptionDetail detail = new SubscriptionDetail(
                "detail-123", uri, customerId, "target",
                financeName, CurrencyType.USD, 10.0, null,
                AssetType.STOCK, NotificationType.EMAIL, LocalDateTime.now()
        );
        subscription.getSubscriptionDetails().add(detail);
        when(subscriptionRepository.findByUri(uri)).thenReturn(Optional.of(subscription));

        subscriptionService.deleteSubscription(customerId, uri, "detail-123");

        assertTrue(subscription.getSubscriptionDetails().isEmpty());
        verify(subscriptionRepository).save(subscription);
    }

    @Test
    void findAllCustomerSubscriptions_NoSubscriptions_ReturnsEmptyList() {
        when(subscriptionRepository.findAll()).thenReturn(Collections.emptyList());

        List<SubscriptionDetailDTO> result = subscriptionService.findAllCustomerSubscriptions(customerId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllCustomerSubscriptions_WithDetails_ReturnsFilteredDTOs() {
        Subscription subscription = new Subscription();
        subscription.setUri(uri);

        SubscriptionDetail detail1 = new SubscriptionDetail(
                "detail-1",
                uri,
                customerId,
                "target",
                financeName,
                CurrencyType.USD,
                10.0,
                null,
                AssetType.STOCK,
                NotificationType.EMAIL,
                LocalDateTime.now()
        );

        SubscriptionDetail detail2 = new SubscriptionDetail(
                "detail-2",
                uri,
                "other-customer",
                "target",
                financeName,
                CurrencyType.EUR,
                1.0,
                null,
                AssetType.BOND,
                NotificationType.SMS,
                LocalDateTime.now()
        );

        subscription.getSubscriptionDetails().addAll(List.of(detail1, detail2));

        when(subscriptionRepository.findAll()).thenReturn(List.of(subscription));
        when(detailDTOMapper.toDTO(eq(detail1), any(String.class)))
                .thenReturn(new SubscriptionDetailDTO(
                        "detail-1",
                        financeName,
                        uri,
                        price,
                        10.0,
                        null,
                        "STOCK",
                        "EMAIL",
                        "USD",
                        detail1.createdDate()
                ));

        FinanceResponse financeResponse = new FinanceResponse(
                financeName,
                "SYM",
                uri,
                UnitType.UNIT,
                price,
                CurrencyType.USD,
                assetType,
                LocalDateTime.now()
        );

        when(financeClient.findFinanceByTypeAndUri(any(), any()))
                .thenReturn(financeResponse);

        List<SubscriptionDetailDTO> result =
                subscriptionService.findAllCustomerSubscriptions(customerId);

        assertEquals(1, result.size());
        verify(detailDTOMapper).toDTO(eq(detail1), eq(price));
    }

    @Test
    void resolveCurrentPrice_CurrencyConversionApplied() {
        SubscriptionDetail detail = new SubscriptionDetail(
                "detail-1", uri, customerId, "target",
                financeName, CurrencyType.EUR, 10.0, null,
                AssetType.STOCK, NotificationType.EMAIL, LocalDateTime.now()
        );

        BigDecimal exchangeRate = BigDecimal.valueOf(0.85);
        FinanceResponse financeResponse = new FinanceResponse(
                financeName, "SYM", uri, UnitType.UNIT, "100", CurrencyType.USD, assetType, LocalDateTime.now()
        );
        when(financeClient.findFinanceByTypeAndUri(any(), any()))
                .thenReturn(financeResponse);
        when(financeClient.findExchangeRate(CurrencyType.USD, CurrencyType.EUR))
                .thenReturn(exchangeRate);

        String result = subscriptionService.resolveCurrentPrice(detail);

        assertEquals(BigDecimal.valueOf(100).multiply(exchangeRate), new BigDecimal(result));
    }
}