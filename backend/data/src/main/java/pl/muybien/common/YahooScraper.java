package pl.muybien.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.updater.QueueUpdater;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class YahooScraper extends QueueUpdater {
    private final SeleniumHandler seleniumHandler;

    private String targetUrl;

    public abstract void scheduleUpdate();
    public abstract void updateAssets();
    protected abstract void scrapePages(WebDriver driver, int startPage, int endPage, Map<String, FinanceDetail> assets);

    protected void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    protected void awaitCompletion(ExecutorService executor, List<Future<?>> futures) {
        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Scraping task failed", e);
            }
        });
        executor.shutdown();
    }

    protected Runnable createScrapingTask(int startPage, int endPage, Map<String, FinanceDetail> assets) {
        return () -> {
            WebDriver driver = null;
            try {
                driver = seleniumHandler.getDriverAndNavigate(targetUrl + "0&count=100");
                WebDriverWait wait = seleniumHandler.getDriverWait(driver, Duration.ofMillis(100));
                seleniumHandler.handleCookieConsent(wait, "button[type='submit'][name='agree']");
                scrapePages(driver, startPage, endPage, assets);
            } catch (Exception e) {
                log.error("Thread error: {}", e.getMessage());
            } finally {
                if (driver != null) {
                    driver.quit();
                }
                System.gc();
            }
        };
    }
}