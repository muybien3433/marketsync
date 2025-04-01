//package pl.muybien.finance.crypto;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.*;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.remote.RemoteWebDriver;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import pl.muybien.finance.AssetType;
//import pl.muybien.finance.CurrencyType;
//import pl.muybien.finance.FinanceDetail;
//import pl.muybien.finance.UnitType;
//import pl.muybien.finance.updater.FinanceDatabaseUpdater;
//import pl.muybien.finance.updater.FinanceUpdater;
//
//import java.math.BigDecimal;
//import java.net.URL;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.IntStream;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CoinmarketcapScraper extends FinanceUpdater {
//
//    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";
//
//    private final FinanceDatabaseUpdater databaseUpdater;
//
//    @Override
//    @EventListener(ApplicationReadyEvent.class)
//    @Scheduled(fixedRateString = "${coinmarketcap.crypto-updater-frequency-ms}")
//    public void scheduleUpdate() {
//        enqueueUpdate("coinmarketcap");
//    }
//
//    @Override
//    @Transactional
//    public void updateAssets() {
//        log.info("Starting the update of CoinMarketCap data...");
//        WebDriver driver = null;
//        try {
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments(
//                    "--no-sandbox",
//                    "--disable-dev-shm-usage",
//                    "--headless=new",
//                    "--window-size=1920,1080",
//                    "--disable-blink-features=AutomationControlled",
//                    "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
//            );
//            options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
//            options.setExperimentalOption("useAutomationExtension", false);
//
//            driver = new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), options);
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//
//            driver.get("https://coinmarketcap.com");
//            handleCookieConsent(wait, driver);
//
//            wait.until(ExpectedConditions.visibilityOfElementLocated(
//                    By.cssSelector("table.cmc-table")));
//
//            scrapAndSaveDataToDatabase(driver, wait);
//            log.info("Finished updating CoinMarketCap data");
//
//        } catch (Exception e) {
//            log.error("Critical error in coinmarketcap: {}", e.getMessage(), e);
//        } finally {
//            if (driver != null) driver.quit();
//        }
//    }
//
//    private void handleCookieConsent(WebDriverWait wait, WebDriver driver) {
//        try {
//            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(
//                    By.xpath("//button[contains(text(), 'Accept')] | //button[contains(@class, 'sc-')]")));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", acceptButton);
//        } catch (TimeoutException e) {
//            log.info("No cookie dialog found.");
//        }
//    }
//
//    private void scrapAndSaveDataToDatabase(WebDriver driver, WebDriverWait wait) {
//        try {
//            wait.until(driv -> driv.findElements(By.cssSelector("table[class*='cmc-table'] tbody tr")).size() >= 30);
//            AtomicReference<List<WebElement>> rows = new AtomicReference<>(driver.findElements(By.cssSelector("table[class*='cmc-table'] tbody tr")));
//            var cryptos = new HashMap<String, FinanceDetail>();
//
//            WebElement table = driver.findElement(By.cssSelector("table[class*='cmc-table']"));
//            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", table);
//
//            new WebDriverWait(driver, Duration.ofSeconds(2))
//                    .until(_ -> !rows.get().isEmpty() && rows.get().getFirst().isDisplayed());
//
//            IntStream.range(0, Math.min(rows.get().size(), 14))
//                    .forEach(i -> {
//                        for (int attempt = 0; attempt < 2; attempt++) {
//                            try {
//                                WebElement row = rows.get().get(i);
//
//                                WebElement nameElement = row.findElement(By.cssSelector("p[class*='coin-item-name']"));
//                                WebElement symbolElement = row.findElement(By.cssSelector("p[class*='coin-item-symbol']"));
//                                WebElement priceElement = row.findElement(By.cssSelector("div[class*='sc-142c02c-0'] > span"));
//
//                                String name = nameElement.getText().trim();
//                                String symbol = symbolElement.getText().replaceAll("[^a-zA-Z0-9]", "").trim();
//                                String priceText = priceElement.getText().replaceAll("[^0-9.]", "");
//
//                                if (!name.isBlank() && !symbol.isBlank() && !priceText.isBlank()) {
//                                    try {
//                                        String price = priceText;
//                                        if (new BigDecimal(price).compareTo(BigDecimal.ZERO) > 0) {
//                                            String uri = name.replaceAll("[ .()]", "-").toLowerCase();
//
//                                            cryptos.put(uri, new FinanceDetail(
//                                                    name,
//                                                    symbol,
//                                                    uri,
//                                                    UnitType.UNIT.name(),
//                                                    price,
//                                                    CurrencyType.USD.name(),
//                                                    AssetType.CRYPTO.name(),
//                                                    LocalDateTime.now()
//                                            ));
//                                        }
//                                    } catch (NumberFormatException e) {
//                                        log.warn("Skipping invalid price for {}: {}", name, priceText);
//                                    }
//                                }
//                                break;
//                            } catch (StaleElementReferenceException e) {
//                                log.warn("Stale element at index {}, refetching (attempt {}/2)...", i, attempt + 1);
//                                rows.set(driver.findElements(By.cssSelector("table[class*='cmc-table'] tbody tr")));
//                            } catch (Exception e) {
//                                if (attempt == 1) {
//                                    log.error("Error processing row {}: {}", i, e.getMessage());
//                                }
//                            }
//                        }
//                    });
//            if (!cryptos.isEmpty()) {
//                databaseUpdater.saveFinanceToDatabase(AssetType.CRYPTO.name(), cryptos);
//            }
//        } finally {
//            driver.quit();
//        }
//    }
//}