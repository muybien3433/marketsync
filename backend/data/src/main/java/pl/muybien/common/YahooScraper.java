package pl.muybien.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.updater.QueueUpdater;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class YahooScraper extends QueueUpdater {
    private String targetUrl;
    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";

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
            RemoteWebDriver driver = null;
            try {
                driver = new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), createChromeOptions());
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100));
                handleCookieConsent(driver, wait);
                scrapePages(driver, startPage, endPage, assets);
            } catch (Exception e) {
                log.error("Thread error: {}", e.getMessage());
            } finally {
                if (driver != null) driver.quit();
            }
        };
    }

    private void handleCookieConsent(WebDriver driver, WebDriverWait wait) {
        try {
            driver.get(targetUrl + "0&count=100");
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[type='submit'][name='agree']")));
            acceptButton.click();
            log.debug("Cookie consent accepted.");
        } catch (TimeoutException e) {
            log.debug("No cookie consent found.");
        }
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
}