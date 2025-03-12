package pl.muybien.finance.crypto;

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
import pl.muybien.finance.AssetType;
import pl.muybien.finance.CurrencyType;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.updater.FinanceDatabaseUpdater;
import pl.muybien.finance.updater.FinanceUpdater;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinmarketcapScraper extends FinanceUpdater {

    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";

    private final FinanceDatabaseUpdater databaseUpdater;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedRateString = "${coinmarketcap.crypto-updater-frequency-ms}")
    public void scheduleUpdate() {
        enqueueUpdate("coinmarketcap");
    }

    @Override
    @Transactional
    public void updateAssets() {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--headless=new",
                    "--window-size=1920,1080",
                    "--disable-blink-features=AutomationControlled",
                    "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
            );
            options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            driver.get("https://coinmarketcap.com");
            handleCookieConsent(wait, driver);

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("table.cmc-table")));

            scrapDataFromFirstSection(driver, wait);

        } catch (Exception e) {
            log.error("Critical error: {}", e.getMessage(), e);
        } finally {
            if (driver != null) driver.quit();
        }
    }

    private void handleCookieConsent(WebDriverWait wait, WebDriver driver) {
        try {
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Accept')] | //button[contains(@class, 'sc-')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptButton);
        } catch (TimeoutException e) {
            log.info("No cookie dialog found.");
        }
    }

    private void scrapDataFromFirstSection(WebDriver driver, WebDriverWait wait) {
        wait.until(driv -> !driv.findElements(By.cssSelector("table[class*='cmc-table'] tbody tr")).isEmpty());
        List<WebElement> rows = driver.findElements(By.cssSelector("table[class*='cmc-table'] tbody tr"));

        int count = 0;
        for (int i = 0; i < rows.size() && count < 110; i++) {
            WebElement row = null;
            try {
                row = wait.until(ExpectedConditions.visibilityOf(
                        driver.findElements(By.cssSelector("table[class*='cmc-table'] tbody tr")).get(i)
                ));

                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", row);

                WebElement nameElement = wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                        row,
                        By.xpath(".//p[contains(@class, 'coin-item-name')]")
                )).getFirst();

                WebElement symbolElement = wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                        row,
                        By.xpath(".//p[contains(@class, 'coin-item-symbol')]")
                )).getFirst();

                WebElement priceElement = wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                        row,
                        By.xpath(".//div[contains(@class, 'sc-142c02c-0')]/span")
                )).getFirst();

                String name = nameElement.getText().trim();
                String symbol = symbolElement.getText().replaceAll("[^a-zA-Z0-9]", "").trim();
                String price = priceElement.getText().trim();

                log.info("Scraped: {} ({}) - {}", name, symbol, price);
                count++;
            } catch (StaleElementReferenceException e) {
                log.warn("Stale element at index {}, refetching...", i);
                rows = driver.findElements(By.cssSelector("table[class*='cmc-table'] tbody tr"));
                i = Math.max(i - 1, 0);
            } catch (Exception e) {
                log.error("Error processing row {}: {}", i, e.getMessage());
            }
        }
    }
}