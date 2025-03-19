package pl.muybien.finance.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.updater.FinanceUpdater;
import pl.muybien.finance.CurrencyType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class WiseScraper extends FinanceUpdater implements CurrencyService {

    private final CurrencyRepository repository;

    @Value("${wise.max-fetch-retries}")
    private Integer maxFetchRetries;
    @Value("${wise.retry-delay-ms}")
    private Integer retryDelayMs;
    @Value("${wise.base-url}")
    private String baseUrl;
    @Value("${wise.url-separator}")
    private String urlSeparator;
    @Value("${wise.rate-amount}")
    private String rateAmount;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedRateString = "${wise.currencyType-updater-frequency-ms}")
    protected void scheduleUpdate() {
        enqueueUpdate("wise");
    }

    @Override
    protected void updateAssets() {
        log.info("Starting the update of Wise data...");
        var allCurrencies = Arrays.asList(CurrencyType.values());

        for (int i = 0; i < allCurrencies.size(); i++) {
            for (int j = 0; j < allCurrencies.size(); j++) {
                if (i != j) {
                    CurrencyType from = allCurrencies.get(i);
                    CurrencyType to = allCurrencies.get(j);

                    calculateAndSaveNewExchangeRate(from, to);
                }
            }
        }
        log.info("Finished update Wise data");
    }

    @Override
    public BigDecimal getCurrencyPairExchange(CurrencyType from, CurrencyType to) {
        var exchange = repository.findCurrencyByName(currencyNameResolver(from, to))
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Could not find currencyType pair for " + from + " to " + to));

        return exchange.getExchange();
    }

    private void calculateAndSaveNewExchangeRate(CurrencyType from, CurrencyType to) {
        BigDecimal exchangeRate = fetchExchangeRate(from, to);
        if (exchangeRate != null && exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            String name = currencyNameResolver(from, to);
            repository.findCurrencyByName(name)
                    .ifPresentOrElse(
                            currency -> {
                                currency.setExchange(exchangeRate);
                                repository.save(currency);
                            },
                            () -> repository.save(
                                    Currency.builder()
                                            .name(name)
                                            .exchange(exchangeRate)
                                            .build()
                            )
                    );
        }
    }

    private BigDecimal fetchExchangeRate(CurrencyType from, CurrencyType to) {
        String url = baseUrl + from + urlSeparator + to + rateAmount;

        for (int attempt = 1; attempt <= maxFetchRetries; attempt++) {
            try {
                Document doc = Jsoup.connect(url).get();
                Element element = doc.select("span.text-success").first();
                if (element != null) {
                    String rateText = element.text().replace(",", "");
                    return new BigDecimal(rateText);
                }
                log.warn("No exchange rate found for {} to {} on attempt {}", from, to, attempt);
            } catch (IOException e) {
                log.error("Failed to fetch exchange rate from {} to {} on attempt {}", from, to, attempt, e);
                if (attempt < maxFetchRetries) {
                    try {
                        Thread.sleep(retryDelayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry interrupted for {} to {}", from, to, ie);
                        break;
                    }
                }
            }
        }
        log.error("All attempts to fetch exchange rate from {} to {} have failed", from, to);
        return null;
    }

    private String currencyNameResolver(CurrencyType from, CurrencyType to) {
        return from.name().toLowerCase() + "-" + to.name().toLowerCase();
    }
}
