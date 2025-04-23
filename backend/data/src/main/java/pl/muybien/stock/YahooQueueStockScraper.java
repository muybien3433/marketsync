package pl.muybien.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.enums.UnitType;
import pl.muybien.updater.DatabaseUpdater;
import pl.muybien.updater.QueueUpdater;

import java.net.URL;
import java.time.Duration;
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
public class YahooQueueStockScraper extends QueueUpdater {

    private static final String TARGET_URL = "https://finance.yahoo.com/markets/stocks/most-active/?start=";
    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";
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

    private Runnable createScrapingTask(int startPage, int endPage, Map<String, FinanceDetail> stocks) {
        return () -> {
            RemoteWebDriver driver = null;
            try {
                driver = new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), createChromeOptions());
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100));
                handleCookieConsent(driver, wait);
                scrapePages(driver, startPage, endPage, stocks);
            } catch (Exception e) {
                log.error("Thread error: {}", e.getMessage());
            } finally {
                if (driver != null) driver.quit();
            }
        };
    }

    private void handleCookieConsent(WebDriver driver, WebDriverWait wait) {
        try {
            driver.get(TARGET_URL + "0&count=100");
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'][name='agree']")));
            acceptButton.click();
            log.debug("Cookie consent accepted.");
        } catch (TimeoutException e) {
            log.debug("No cookie consent found.");
        }
    }

    private void scrapePages(WebDriver driver, int startPage, int endPage,
                             Map<String, FinanceDetail> stocks) throws InterruptedException {
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
                    Thread.sleep(RETRY_DELAY_MS * (attempt + 1));
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

    private ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-extensions",
                "--disable-popup-blocking",
                "--blink-settings=imagesEnabled=false",
                "--disable-logging"
        );
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        return options;
    }

    private void awaitCompletion(ExecutorService executor, List<Future<?>> futures) {
        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Scraping task failed", e);
            }
        });
        executor.shutdown();
    }
}


