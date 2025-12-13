//package pl.muybien.commodity;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.By;
//import org.openqa.selenium.NoSuchElementException;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import pl.muybien.common.SeleniumHandler;
//import pl.muybien.enumeration.AssetType;
//import pl.muybien.enumeration.CurrencyType;
//import pl.muybien.enumeration.UnitType;
//import pl.muybien.entity.helper.FinanceDetail;
//import pl.muybien.exception.FinanceNotFoundException;
//import pl.muybien.updater.DatabaseUpdater;
//import pl.muybien.updater.QueueUpdater;
//
//import java.math.BigDecimal;
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//
//import static pl.muybien.enumeration.CurrencyType.extractCurrency;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class TradingEconomicsScraper extends QueueUpdater {
//
//    private static final String TARGET_URL = "https://tradingeconomics.com/commodities";
//
//    private final DatabaseUpdater databaseUpdater;
//    private final SeleniumHandler seleniumHandler;
//
//    @Override
//    @EventListener(ApplicationReadyEvent.class)
//    @Scheduled(fixedDelay = 60000)
//    public void scheduleUpdate() {
//        enqueueUpdate("trading-economics");
//    }
//
//    @Override
//    public void updateAssets() {
//        var commodities = new HashMap<String, FinanceDetail>();
//
//        WebDriver driver = null;
//        try {
//            driver = seleniumHandler.getDriverAndNavigate(TARGET_URL);
//            driver.manage().window().maximize();
//            WebDriverWait wait = seleniumHandler.getDriverWait(driver, Duration.ofSeconds(10));
//
//            seleniumHandler.handleCookieConsent(wait, By.xpath("//button[contains(., 'Consent')]"));
//
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table-hover tbody tr")));
//            List<WebElement> rows = driver.findElements(By.cssSelector("table.table-hover tbody tr[data-symbol]"));
//
//            for (WebElement row : rows) {
//                try {
//                    WebElement nameElement = row.findElement(By.cssSelector("td.datatable-item-first b"));
//                    String name = nameElement.getText().trim();
//
//                    WebElement unitElement = row.findElement(
//                            By.cssSelector("td.datatable-item-first div[style*='font-size: 10px;']"));
//                    String unitText = unitElement.getText().trim();
//
//                    WebElement priceElement = row.findElement(By.cssSelector("td#p.datatable-item"));
//                    BigDecimal price = new BigDecimal(priceElement.getText().trim());
//
//                    WebElement linkElement = row.findElement(By.cssSelector("td.datatable-item-first a"));
//                    String href = linkElement.getAttribute("href");
//                    String uri = href.substring(href.lastIndexOf("/") + 1);
//
//                    CurrencyType currencyType = extractCurrency(unitText);
//                    UnitType unitType = UnitType.extractUnit(unitText);
//
//                    if (currencyType != null && unitType != null) {
//                        FinanceDetail detail = new FinanceDetail(
//                                name,
//                                null,
//                                uri,
//                                unitType,
//                                price,
//                                currencyType,
//                                AssetType.COMMODITY,
//                                LocalDateTime.now(),
//                                0
//                        );
//                        commodities.put(uri, detail);
//                    }
//                } catch (NoSuchElementException e) {
//                    log.warn("Skipping row due to missing elements: {}", e.getMessage());
//                }
//            }
//            databaseUpdater.saveFinanceToDatabase(AssetType.COMMODITY, commodities);
//        } catch (Exception e) {
//            throw new FinanceNotFoundException("TradingEconomics data: " + e.getMessage());
//        } finally {
//            if (driver != null) driver.quit();
//            System.gc();
//        }
//    }
//}
