package pl.muybien.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.common.YahooScraper;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.enums.UnitType;
import pl.muybien.updater.DatabaseUpdater;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public class YahooQueueStockScraper extends YahooScraper {

    private static final String TARGET_URL = "https://finance.yahoo.com/markets/stocks/most-active/?start=";
    private static final int THREAD_POOL_SIZE = 2;
    private static final int RETRY_ATTEMPTS = 4;
    private static final int PAGE_DELAY_MS = 500;
    private static final int RETRY_DELAY_MS = 2000;
    private static final int REGULAR_PAGES = 6;  // Pages 0-5 (6 pages)

    private final DatabaseUpdater databaseUpdater;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 75000)
    public void scheduleUpdate() {
        enqueueUpdate("yahoo-finance-stock");
    }

    @Override
    @Transactional
    public void updateAssets() {
        log.info("Starting the update of YahooFinanceStock data...");

        setTargetUrl(TARGET_URL);

        long startTime = System.currentTimeMillis();
        int totalPages;
        int startPageOffset;

        totalPages = REGULAR_PAGES;
        startPageOffset = 0;
        log.debug("Starting scrape (pages 0-2)");

        Map<String, FinanceDetail> stocks = new ConcurrentHashMap<>(1200);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<?>> futures = new ArrayList<>();

        int pagesPerThread = (int) Math.ceil((double) totalPages / THREAD_POOL_SIZE);

        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            int threadStart = startPageOffset + i * pagesPerThread;
            int threadEnd = Math.min(
                    threadStart + pagesPerThread - 1,
                    startPageOffset + totalPages - 1
            );

            if (threadStart > threadEnd) continue;

            futures.add(executor.submit(createScrapingTask(threadStart, threadEnd, stocks)));
        }

        awaitCompletion(executor, futures);
        long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
        log.debug("Total operation completed in {} seconds", durationSeconds);

        long persistStartTime = System.currentTimeMillis();
        if (!stocks.isEmpty()) {
            databaseUpdater.saveFinanceToDatabase(AssetType.STOCK.name(), stocks);
        }

        long durationDataPersisting = (System.currentTimeMillis() - persistStartTime) / 1000;
        log.debug("Data saved in {} seconds", durationDataPersisting);

        log.info("Finished updating YahooFinanceStock data");
    }

    protected void scrapePages(WebDriver driver, int startPage, int endPage, Map<String, FinanceDetail> stocks) {
        log.debug("Scraping pages {}-{}", startPage, endPage);

        final String rowCheckScript =
                "var rows = document.querySelectorAll('table.markets-table tbody tr');" +
                        "return rows.length > 0 && " +
                        "rows[0].querySelector('td:nth-child(2)') && " +
                        "rows[0].querySelector('td:nth-child(2)').textContent.trim() !== ''";

        for (int page = startPage; page <= endPage; page++) {
            int startParam = page * 100;
            String url = TARGET_URL + startParam + "&count=100";

            for (int attempt = 0; attempt < RETRY_ATTEMPTS; attempt++) {
                try {
                    driver.get(url);

                    long start = System.currentTimeMillis();
                    while (System.currentTimeMillis() - start < 1500) {
                        if ((Boolean) ((JavascriptExecutor) driver).executeScript(rowCheckScript)) {
                            break;
                        }
                    }

                    extractData(driver, stocks);
                    Thread.sleep(PAGE_DELAY_MS);
                    break;
                } catch (TimeoutException e) {
                    log.warn("Timeout page {} (attempt {})", page, attempt + 1);
                    try {
                        Thread.sleep(RETRY_DELAY_MS * (attempt + 1));
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (Exception e) {
                    log.error("Page {} error: {}", page, e.getMessage());
                    if (attempt == RETRY_ATTEMPTS - 1) {
                        log.error("Failed page {} after {} attempts", page, RETRY_ATTEMPTS);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void extractData(WebDriver driver, Map<String, FinanceDetail> stocks) {
        log.debug("Finding element by css");
        String xpath = "//div[contains(@class, 'tableContainer')]//table[contains(@class, 'bd')]";
        WebElement table = driver.findElement(By.xpath(xpath));

        String script =
                "return Array.from(arguments[0].querySelectorAll('tbody tr')).map(row => " +
                        "Array.from(row.querySelectorAll('td:not(.hidden)')).slice(0,4).map(cell => cell.innerText.trim()))";

        List<List<String>> allData = (List<List<String>>) ((JavascriptExecutor) driver)
                .executeScript(script, table);

        log.debug("Found {} rows starting processing", allData.size());
        for (List<String> cells : allData) {
            if (cells.size() >= 4) {
                String symbol = cells.get(0);
                String name = cells.get(1);
                String price = cells.get(3);
                String cleanedPrice = price.replace(",", "").replaceAll("[^\\d.\\-]", "");

                if (!symbol.isEmpty() && !name.isEmpty() && !cleanedPrice.isEmpty()) {
                    String uri = name.trim().toLowerCase()
                            .replaceAll("\\.", "").replaceAll(" ", "-");

                    stocks.put(uri, new FinanceDetail(
                            name, symbol, uri,
                            UnitType.UNIT.name(),
                            cleanedPrice,
                            CurrencyType.USD.name(),
                            AssetType.STOCK.name(),
                            LocalDateTime.now()
                    ));
                }
            }
        }
        log.debug("Finished processing rows");
    }
}


