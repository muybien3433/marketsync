package pl.muybien.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import pl.muybien.updater.QueueUpdater;
import reactor.core.publisher.Mono;
import pl.muybien.entity.helper.FinanceDetail;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.UnitType;
import pl.muybien.updater.DatabaseUpdater;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Slf4j
@Transactional
public abstract class YahooScraper extends QueueUpdater {

    protected static final int PAGE_SIZE = 250;
    protected static final int RETRY_ATTEMPTS = 4;
    protected static final int PAGE_DELAY_MS = 0;
    protected static final int RETRY_DELAY_MS = 5000;

    protected final WebClient yahooWebClient;
    protected final DatabaseUpdater databaseUpdater;
    protected final Random random = new Random();

    protected YahooScraper(WebClient yahooWebClient, DatabaseUpdater databaseUpdater) {
        this.yahooWebClient = yahooWebClient;
        this.databaseUpdater = databaseUpdater;
    }

    protected abstract AssetType getAssetType();

    protected abstract CurrencyType getCurrencyType();

    protected abstract UnitType getUnitType();

    protected abstract String getScreenerId();

    protected List<String> getScreenerIds() {
        return Collections.singletonList(getScreenerId());
    }

    protected String getFields() {
        return "ticker,companyName,regularMarketPrice";
    }

    protected String normalizeSymbol(String ticker) {
        return ticker;
    }

    protected String normalizeName(String companyName) {
        return companyName;
    }

    @Override
    public final void updateAssets() {
        Map<String, FinanceDetail> details = new ConcurrentHashMap<>(16000);

        for (String screenerId : getScreenerIds()) {
            scrapeScreener(screenerId, details);
        }

        if (!details.isEmpty()) {
            databaseUpdater.saveFinanceToDatabase(getAssetType(), details);
        }
    }

    private void scrapeScreener(String screenerId, Map<String, FinanceDetail> target) {
        ScreenerResponse firstResponse = fetchPageWithRetries(screenerId, 0, PAGE_SIZE);
        if (!isResponseValid(firstResponse)) {
            log.warn("Invalid first response for screener={} start=0 pageSize={}", screenerId, PAGE_SIZE);
            return;
        }

        ScreenerResponse.Result firstResult = extractResult(screenerId, 0, firstResponse);
        if (firstResult == null || firstResult.records == null || firstResult.records.isEmpty()) {
            log.warn("No records in first page for screener={} start=0 pageSize={}", screenerId, PAGE_SIZE);
            return;
        }

        int total = firstResult.total != null ? firstResult.total : firstResult.count != null ? firstResult.count : 0;
        if (total <= 0) {
            log.warn("Total records not reported or zero in first page for screener={}", screenerId);
            return;
        }

        log.debug("Total records reported by Yahoo for screener {}: {}", screenerId, total);

        processRecords(firstResult.records, 0, target);

        int pages = (int) Math.ceil(total / (double) PAGE_SIZE);
        if (pages > 1) {
            IntStream.range(1, pages)
                    .parallel()
                    .forEach(page -> {
                        int start = page * PAGE_SIZE;
                        ScreenerResponse response = fetchPageWithRetries(screenerId, start, PAGE_SIZE);
                        if (!isResponseValid(response)) {
                            log.warn("Invalid response for screener={} start={} pageSize={}", screenerId, start, PAGE_SIZE);
                            return;
                        }
                        ScreenerResponse.Result result = extractResult(screenerId, start, response);
                        if (result == null || result.records == null || result.records.isEmpty()) {
                            log.debug("No records for screener={} start={} pageSize={}", screenerId, start, PAGE_SIZE);
                            return;
                        }
                        processRecords(result.records, start, target);
                    });
        }
    }

    protected ScreenerResponse fetchPageWithRetries(String screenerId, int start, int count) {
        for (int attempt = 0; attempt < RETRY_ATTEMPTS; attempt++) {
            try {
                return fetchPage(screenerId, start, count);
            } catch (Exception e) {
                log.warn("Error scraping screener={} start={} attempt {}: {}", screenerId, start, attempt + 1, e.getMessage());
                if (attempt == RETRY_ATTEMPTS - 1) {
                    log.error("Failed screener={} start={} after {} attempts", screenerId, start, RETRY_ATTEMPTS);
                    return null;
                }
                try {
                    Thread.sleep((long) RETRY_DELAY_MS * (attempt + 1) + random.nextInt(500));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }

        return null;
    }

    protected boolean isResponseValid(ScreenerResponse response) {
        if (response == null || response.finance == null) {
            return false;
        }
        if (response.finance.error != null) {
            log.error("Yahoo error code={} description={}",
                    response.finance.error.code,
                    response.finance.error.description
            );
            return false;
        }
        return response.finance.result != null && !response.finance.result.isEmpty();
    }

    protected ScreenerResponse.Result extractResult(String screenerId, int start, ScreenerResponse response) {
        if (response == null || response.finance == null || response.finance.result == null || response.finance.result.isEmpty()) {
            log.warn("Empty result for screener={} start={} pageSize={}", screenerId, start, PAGE_SIZE);
            return null;
        }
        return response.finance.result.get(0);
    }

    protected void processRecords(List<ScreenerResponse.Record> records, int pageStart, Map<String, FinanceDetail> target) {
        if (records == null || records.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int index = 0;

        for (ScreenerResponse.Record rec : records) {
            if (rec == null || rec.regularMarketPrice == null) {
                continue;
            }

            String rawSymbol = rec.ticker != null ? rec.ticker : rec.symbol;
            if (rawSymbol == null) {
                continue;
            }

            String rawName =
                    rec.companyName != null ? rec.companyName :
                            rec.longName != null ? rec.longName :
                                    rec.shortName != null ? rec.shortName :
                                            rawSymbol;

            String symbol = normalizeSymbol(rawSymbol);
            String name = normalizeName(rawName);
            BigDecimal price = BigDecimal.valueOf(rec.regularMarketPrice);

            if (symbol.isEmpty() || name.isEmpty()) {
                continue;
            }

            String uri = buildUri(name);
            int position = pageStart + index++;

            FinanceDetail detail = new FinanceDetail(
                    name,
                    symbol,
                    uri,
                    getUnitType(),
                    price,
                    getCurrencyType(),
                    getAssetType(),
                    now,
                    position
            );

            target.put(uri, detail);
        }
    }

    protected ScreenerResponse fetchPage(String screenerId, int start, int count) {
        return yahooWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/finance/screener/predefined/saved")
                        .queryParam("count", count)
                        .queryParam("formatted", "false")
                        .queryParam("scrIds", screenerId)
                        .queryParam("sortField", "")
                        .queryParam("sortType", "")
                        .queryParam("start", start)
                        .queryParam("useRecordsResponse", "true")
                        .queryParam("fields", getFields())
                        .queryParam("lang", "en-US")
                        .queryParam("region", "US")
                        .build())
                .exchangeToMono(response -> {
                    var status = response.statusCode();
                    if (status.is2xxSuccessful()) {
                        return response.bodyToMono(ScreenerResponse.class);
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                log.error("Yahoo HTTP error status={} screener={} start={} count={} body={}",
                                        status.value(), screenerId, start, count, body);
                                return Mono.error(new IllegalStateException("Yahoo error " + status.value()));
                            });
                })
                .timeout(Duration.ofSeconds(10))
                .block();
    }

    protected String buildUri(String name) {
        return name.replaceAll("[ .()]", "-").toLowerCase();
    }

    protected static class ScreenerResponse {
        public Finance finance;

        protected static class Finance {
            public List<Result> result;
            public Error error;
        }

        protected static class Error {
            public String code;
            public String description;
        }

        protected static class Result {
            public Integer total;
            public Integer count;
            public Integer start;
            public List<Record> records;
        }

        protected static class Record {
            public String ticker;
            public String symbol;
            public String companyName;
            public String shortName;
            public String longName;
            public Double regularMarketPrice;
        }
    }
}
