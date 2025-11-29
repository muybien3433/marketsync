package pl.muybien.crypto;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.common.SeleniumHandler;
import pl.muybien.common.YahooScraper;
import pl.muybien.enumeration.AssetType;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.entity.helper.FinanceDetail;
import pl.muybien.enumeration.UnitType;
import pl.muybien.updater.DatabaseUpdater;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class YahooCryptoScraper extends YahooScraper {

    private static final String TARGET_URL = "https://finance.yahoo.com/markets/crypto/all/?start=";
    private static final int THREAD_POOL_SIZE = 1;
    private static final int RETRY_ATTEMPTS = 4;
    private static final int PAGE_DELAY_MS = 200;
    private static final int RETRY_DELAY_MS = 5000;
    private static final int REGULAR_PAGES = 6;  // Pages 0-5 (6 pages)
    private static final int SECOND_SECTION_PAGES = 25;  // Pages 6-30 (25 pages)
    private static final int THIRD_SECTION_PAGES = 21;   // Pages 30-50 (21 pages)
    private final AtomicInteger invocationCount = new AtomicInteger(1);

    private final DatabaseUpdater databaseUpdater;

    public YahooCryptoScraper(SeleniumHandler seleniumHandler, DatabaseUpdater databaseUpdater) {
        super(seleniumHandler);
        this.databaseUpdater = databaseUpdater;
        log.debug("YahooCryptoScraper initialized");
        log.debug("THREAD_POOL_SIZE: {}", THREAD_POOL_SIZE);
        log.debug("RETRY_ATTEMPTS: {}", RETRY_ATTEMPTS);
        log.debug("PAGE_DELAY_MS: {}", PAGE_DELAY_MS);
        log.debug("RETRY_DELAY_MS: {}", RETRY_DELAY_MS);
        log.debug("REGULAR_PAGES: {}", REGULAR_PAGES);
        log.debug("SECOND_SECTION_PAGES: {}", SECOND_SECTION_PAGES);
        log.debug("THIRD_SECTION_PAGES: {}", THIRD_SECTION_PAGES);
    }

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 65000)
    public void scheduleUpdate() {
        enqueueUpdate("yahoo-finance-crypto");
    }

    @Override
    @Transactional
    public void updateAssets() {
        setTargetUrl(TARGET_URL);

        long startTime = System.currentTimeMillis();
        int invCounter = invocationCount.getAndIncrement();
        int totalPages;
        int startPageOffset;

        if (invCounter % 12 == 0) {
            totalPages = THIRD_SECTION_PAGES;
            startPageOffset = 30;
            log.debug("Starting third section scrape (pages 30-50)");
        } else if (invCounter % 5 == 0) {
            totalPages = SECOND_SECTION_PAGES;
            startPageOffset = 6;
            log.debug("Starting second section scrape (pages 6-30)");
        } else {
            totalPages = REGULAR_PAGES;
            startPageOffset = 0;
            log.debug("Starting regular scrape (pages 0-5)");
        }

        Map<String, FinanceDetail> cryptos = new ConcurrentHashMap<>(14000);
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

            futures.add(executor.submit(createScrapingTask(threadStart, threadEnd, cryptos)));
        }

        awaitCompletion(executor, futures);
        long durationSeconds = (System.currentTimeMillis() - startTime) / 1000;
        log.debug("Total operation completed in {} seconds", durationSeconds);

        long persistStartTime = System.currentTimeMillis();
        if (!cryptos.isEmpty()) {
            databaseUpdater.saveFinanceToDatabase(AssetType.CRYPTO, cryptos);
        }

        long durationDataPersisting = (System.currentTimeMillis() - persistStartTime) / 1000;
        log.debug("Data saved in {} seconds", durationDataPersisting);
    }

    @Override
    protected void scrapePages(WebDriver driver, int startPage, int endPage, Map<String, FinanceDetail> cryptos) {
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

                    extractData(driver, cryptos);
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
    private void extractData(WebDriver driver, Map<String, FinanceDetail> cryptos) {
        log.debug("Finding element by css");
        String xpath = "//div[contains(@class, 'tableContainer')]//table[contains(@class, 'bd')]";
        WebElement table = driver.findElement(By.xpath(xpath));

        String script =
                "return Array.from(arguments[0].querySelectorAll('tbody tr')).map(row => " +
                        "Array.from(row.querySelectorAll('td:not(.hidden)')).slice(0,4).map(cell => cell.innerText.trim()))";

        List<List<String>> allData = (List<List<String>>) ((JavascriptExecutor) driver)
                .executeScript(script, table);

        log.debug("Found {} rows starting processing", allData.size());
        for(List<String> cells : allData) {
            if(cells.size() >=4) {
                String symbol = cells.get(0).substring(0, cells.get(0).length() - 4);
                String name = cells.get(1).substring(0, cells.get(1).length() - 4);
                String price = cells.get(3);
                String cleanedPrice = price.replace(",", "").replaceAll("[^\\d.\\-]", "");

                log.debug("Symbol: {}, Name: {}, Price: {}", symbol, name, cleanedPrice);

                if(!symbol.isEmpty() && !name.isEmpty() && !cleanedPrice.isEmpty()) {
                    String uri = name.replaceAll("[ .()]", "-").toLowerCase();
                    var detail = new FinanceDetail(
                            name,
                            symbol,
                            uri,
                            UnitType.UNIT,
                            cleanedPrice,
                            CurrencyType.USD,
                            AssetType.CRYPTO,
                            LocalDateTime.now()
                    );
                    cryptos.put(uri, detail);
                }
            }
        }
        log.debug("Finished processing rows");
    }
}


