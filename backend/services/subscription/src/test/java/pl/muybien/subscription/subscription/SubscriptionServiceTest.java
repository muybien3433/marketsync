package pl.muybien.subscription.subscription;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import pl.muybien.subscription.customer.CustomerClient;
import pl.muybien.subscription.customer.CustomerResponse;
import pl.muybien.subscription.exception.CustomerNotFoundException;
import pl.muybien.subscription.exception.OwnershipException;
import pl.muybien.subscription.exception.SubscriptionNotFoundException;
import pl.muybien.subscription.finance.FinanceService;
import pl.muybien.subscription.finance.FinanceServiceFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService service;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionDetailRepository subscriptionDetailRepository;

    @Mock
    private FinanceServiceFactory financeServiceFactory;

    @Mock
    private SubscriptionDetailDTOMapper detailDTOMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final String email = "john.doe@example.com";
    private static final String financeName = "FinanceName";
    private static final String authHeader = "Bearer token";
    private static final String uri = "Uri";
    private static final String customerId = "test123";

    @Test
    void createIncreaseSubscription_shouldCreateAndReturnSubscriptionDetail() {
        BigDecimal value = BigDecimal.valueOf(100_000);

        var request = new SubscriptionRequest(uri, value);
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var financeService = mock(FinanceService.class);
        var subscription = Subscription.builder()
                .id(1L)
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .subscriptionDetails(new ArrayList<>())
                .build();
        var createdSubscriptionDetail = SubscriptionDetail.builder()
                .financeName(financeName)
                .upperBoundPrice(value)
                .lowerBoundPrice(null)
                .createdDate(LocalDateTime.now())
                .build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(Optional.of(subscription));
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeService.createIncreaseSubscription(value, email, customerId)).thenReturn(createdSubscriptionDetail);
        when(subscriptionDetailRepository.save(any(SubscriptionDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SubscriptionDetailDTO result = service.createIncreaseSubscription(authHeader, request);

        assertThat(result.financeName()).isEqualTo(financeName);
        assertThat(result.upperBoundPrice()).isEqualTo(value);
        assertThat(result.lowerBoundPrice()).isNull();

        verify(customerClient).fetchCustomerFromHeader(authHeader);
        verify(subscriptionRepository).findByCustomerId(customerId);
        verify(financeServiceFactory).getService(uri);
        verify(financeService).createIncreaseSubscription(value, email, customerId);

        ArgumentCaptor<SubscriptionDetail> captor = ArgumentCaptor.forClass(SubscriptionDetail.class);
        verify(subscriptionDetailRepository).save(captor.capture());
        SubscriptionDetail capturedDetail = captor.getValue();
        assertThat(capturedDetail.getFinanceName()).isEqualTo(financeName);
        assertThat(capturedDetail.getUpperBoundPrice()).isEqualTo(value);
        assertThat(capturedDetail.getLowerBoundPrice()).isNull();
        assertThat(capturedDetail.getSubscription()).isEqualTo(subscription);
    }

    @Test
    void createDecreaseSubscription_shouldCreateAndReturnSubscriptionDetail() {
        BigDecimal value = BigDecimal.valueOf(100_000);

        var request = new SubscriptionRequest(uri, value);
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var financeService = mock(FinanceService.class);
        var subscription = Subscription.builder()
                .id(1L)
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .subscriptionDetails(new ArrayList<>())
                .build();
        var createdSubscriptionDetail = SubscriptionDetail.builder()
                .financeName(financeName)
                .upperBoundPrice(null)
                .lowerBoundPrice(value)
                .createdDate(LocalDateTime.now())
                .build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(Optional.of(subscription));
        when(financeServiceFactory.getService(uri)).thenReturn(financeService);
        when(financeService.createDecreaseSubscription(value, email, customerId)).thenReturn(createdSubscriptionDetail);
        when(subscriptionDetailRepository.save(any(SubscriptionDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SubscriptionDetailDTO result = service.createDecreaseSubscription(authHeader, request);

        assertThat(result.financeName()).isEqualTo(financeName);
        assertThat(result.upperBoundPrice()).isNull();
        assertThat(result.lowerBoundPrice()).isEqualTo(value);

        verify(customerClient).fetchCustomerFromHeader(authHeader);
        verify(subscriptionRepository).findByCustomerId(customerId);
        verify(financeServiceFactory).getService(uri);
        verify(financeService).createDecreaseSubscription(value, email, customerId);

        ArgumentCaptor<SubscriptionDetail> captor = ArgumentCaptor.forClass(SubscriptionDetail.class);
        verify(subscriptionDetailRepository).save(captor.capture());
        SubscriptionDetail capturedDetail = captor.getValue();
        assertThat(capturedDetail.getFinanceName()).isEqualTo(financeName);
        assertThat(capturedDetail.getUpperBoundPrice()).isNull();
        assertThat(capturedDetail.getLowerBoundPrice()).isEqualTo(value);
        assertThat(capturedDetail.getSubscription()).isEqualTo(subscription);
    }

    @Test
    void deleteSubscription_shouldDeleteSubscriptionDetail() {
        Long subscriptionDetailId = 1L;
        Long subscriptionId = 1L;
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var subscription = Subscription.builder()
                .id(subscriptionId)
                .customerId(customerId)
                .createdDate(LocalDateTime.now())
                .build();
        var subscriptionDetail = SubscriptionDetail.builder()
                .id(subscriptionDetailId)
                .customerId(customerId)
                .financeName(financeName)
                .subscription(subscription)
                .build();

        var financeService = mock(FinanceService.class);

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(Optional.of(subscription));
        when(subscriptionDetailRepository.findById(subscriptionDetailId)).thenReturn(Optional.of(subscriptionDetail));
        when(financeServiceFactory.getService(financeName)).thenReturn(financeService);

        service.deleteSubscription(authHeader, subscriptionDetailId);

        verify(customerClient).fetchCustomerFromHeader(authHeader);
        verify(subscriptionRepository).findByCustomerId(customerId);
        verify(subscriptionDetailRepository).findById(subscriptionDetailId);
        verify(financeServiceFactory).getService(financeName);
        verify(financeService).deleteSubscription(subscriptionId, customerId);
        verify(subscriptionDetailRepository).delete(subscriptionDetail);
    }

    @Test
    void deleteSubscription_shouldThrowBusinessExceptionWhenCustomerNotFound() {
        Long subscriptionDetailId = 1L;

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(null);

        assertThatThrownBy(() -> service.deleteSubscription(authHeader, subscriptionDetailId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer not found");
    }

    @Test
    void deleteSubscription_shouldThrowBusinessExceptionWhenSubscriptionNotFound() {
        Long subscriptionDetailId = 1L;
        var customer = new CustomerResponse(customerId, "John", "Doe", email);

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteSubscription(authHeader, subscriptionDetailId))
                .isInstanceOf(SubscriptionNotFoundException.class)
                .hasMessage("Subscription not deleted:: No Subscription exists for customer ID: %s".formatted(customerId));
    }

    @Test
    void deleteSubscription_shouldThrowEntityNotFoundExceptionWhenSubscriptionDetailNotFound() {
        Long subscriptionDetailId = 1L;
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var subscription = Subscription.builder().id(1L).customerId(customerId).build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(Optional.of(subscription));
        when(subscriptionDetailRepository.findById(subscriptionDetailId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteSubscription(authHeader, subscriptionDetailId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Subscription not deleted:: No Subscription exists with ID: %d".formatted(subscriptionDetailId));
    }

    @Test
    void deleteSubscription_shouldThrowOwnershipExceptionWhenCustomerMismatch() {
        String differentCustomerId = "differentCustomerId";
        Long subscriptionDetailId = 1L;
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var subscription = Subscription.builder().id(1L).customerId(customerId).build();
        var subscriptionDetail = SubscriptionDetail.builder()
                .id(subscriptionDetailId)
                .customerId(differentCustomerId)
                .subscription(subscription)
                .build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(Optional.of(subscription));
        when(subscriptionDetailRepository.findById(subscriptionDetailId)).thenReturn(Optional.of(subscriptionDetail));

        assertThatThrownBy(() -> service.deleteSubscription(authHeader, subscriptionDetailId))
                .isInstanceOf(OwnershipException.class)
                .hasMessage("Subscription deletion failed:: Customer id mismatch");
    }

    @Test
    void findAllSubscriptions_shouldDisplaySubscriptionDetailList() {
        var customer = new CustomerResponse(customerId, "John", "Doe", email);
        var subscriptionDetail1 = mock(SubscriptionDetail.class);
        var subscriptionDetail2 = mock(SubscriptionDetail.class);
        var subscriptionDetail3 = mock(SubscriptionDetail.class);

        List<SubscriptionDetail> subscriptionDetaiList = new ArrayList<>();
        subscriptionDetaiList.add(subscriptionDetail1);
        subscriptionDetaiList.add(subscriptionDetail2);
        subscriptionDetaiList.add(subscriptionDetail3);

        var subscription = Subscription.builder()
                .id(1L)
                .subscriptionDetails(subscriptionDetaiList)
                .customerId(customerId)
                .build();

        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(customer);
        when(subscriptionRepository.findByCustomerId(customerId)).thenReturn(Optional.of(subscription));

        List<SubscriptionDetailDTO> result = service.findAllSubscriptions(authHeader);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(detailDTOMapper.mapToDTO(subscriptionDetail3), result.get(0));
        assertEquals(detailDTOMapper.mapToDTO(subscriptionDetail2), result.get(1));
        assertEquals(detailDTOMapper.mapToDTO(subscriptionDetail1), result.get(2));

        verify(detailDTOMapper, times(6)).mapToDTO(any(SubscriptionDetail.class));
        verify(customerClient).fetchCustomerFromHeader(authHeader);
        verify(subscriptionRepository).findByCustomerId(customerId);
    }

    @Test
    void testFindAllSubscriptionsCustomerNotFound() {
        when(customerClient.fetchCustomerFromHeader(authHeader)).thenReturn(null);

        assertThrows(CustomerNotFoundException.class, () -> service.findAllSubscriptions(authHeader));
    }
}