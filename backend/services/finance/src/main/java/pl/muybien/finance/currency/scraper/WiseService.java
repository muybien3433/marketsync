package pl.muybien.finance.currency.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.currency.Currency;
import pl.muybien.finance.currency.CurrencyRepository;
import pl.muybien.finance.currency.CurrencyService;
import pl.muybien.finance.currency.CurrencyType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
public class WiseService implements CurrencyService {

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
    public BigDecimal getCurrencyPairExchange(CurrencyType from, CurrencyType to) {
        var exchange = repository.findCurrencyByName(currencyNameResolver(from, to))
                .orElseThrow(() -> new FinanceNotFoundException(
                        "Could not find currency pair for " + from + " to " + to));

        return exchange.getExchange();
    }

    @Scheduled(fixedRateString = "${wise.currency-updater-frequency-ms}")
    public void currencyPairUpdater() {
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
    }

    private void calculateAndSaveNewExchangeRate(CurrencyType from, CurrencyType to) {
        BigDecimal exchangeRate = fetchExchangeRate(from, to);
        if (exchangeRate != null && exchangeRate.compareTo(BigDecimal.ZERO) > 0) {
            String name = currencyNameResolver(from, to);
            log.info("Saving {} to database", name);

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
                log.info("Attempt {} to fetch exchange rate from {} to {}", attempt, from, to);
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
