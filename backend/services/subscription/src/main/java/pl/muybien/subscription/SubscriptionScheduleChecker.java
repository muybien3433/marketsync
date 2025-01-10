package pl.muybien.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.finance.FinanceClient;
import pl.muybien.subscription.data.Subscription;
import pl.muybien.subscription.data.SubscriptionDetail;
import pl.muybien.subscription.data.SubscriptionRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionScheduleChecker {

    @Value("${schedule.fetch-page-size-per-round}")
    Integer fetchPagesSizePerRound;

    private final SubscriptionComparator subscriptionComparator;
    private final SubscriptionRepository subscriptionRepository;
    private final FinanceClient financeClient;

    @Scheduled(fixedRateString = "${schedule.time-ms}")
    public void compareSubscriptionsWithNewPrice() {
        Pageable pageable = PageRequest.of(0, fetchPagesSizePerRound);
        Page<Subscription> subscriptionPage;

        do {
            subscriptionPage = subscriptionRepository.findAllSubscriptions(pageable);
            subscriptionPage.forEach(this::processSubscription);

            pageable = pageable.next();
        } while (!subscriptionPage.isEmpty());
    }

    void processSubscription(Subscription subscription) {
        for (Map.Entry<String, List<SubscriptionDetail>> entry : subscription.getSubscriptions().entrySet()) {
            String uri = entry.getKey();
            List<SubscriptionDetail> subscriptionDetails = entry.getValue();

            try {
                var finance = financeClient.findFinanceByTypeAndUri("cryptos", uri);
                double currentPrice = finance.price().doubleValue();

                for (SubscriptionDetail subscriptionDetail : subscriptionDetails) {
                    subscriptionComparator.priceMetSubscriptionCondition(currentPrice, subscriptionDetail);
                }
            } catch (Exception e) {
                log.error("Error processing subscription for URI: {}", uri, e);
            }
        }
    }
}

