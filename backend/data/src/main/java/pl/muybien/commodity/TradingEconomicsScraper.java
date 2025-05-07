package pl.muybien.commodity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.muybien.common.SeleniumHandler;
import pl.muybien.enums.AssetType;
import pl.muybien.enums.CurrencyType;
import pl.muybien.enums.UnitType;
import pl.muybien.finance.FinanceDetail;
import pl.muybien.finance.exception.FinanceNotFoundException;
import pl.muybien.updater.DatabaseUpdater;
import pl.muybien.updater.QueueUpdater;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingEconomicsScraper extends QueueUpdater {

    private static final String TARGET_URL = "https://tradingeconomics.com/commodities";

    private final SeleniumHandler seleniumHandler;
    private final DatabaseUpdater databaseUpdater;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedDelay = 60000)
    public void scheduleUpdate() {
        enqueueUpdate("trading-economics");
    }

    @Override
    public void updateAssets() {
        log.info("Starting the update of TradingEconomics data...");
        var commodities = new HashMap<String, FinanceDetail>();

        WebDriver driver = null;
        try {
            driver = seleniumHandler.getDriverAndNavigate(TARGET_URL);
            driver.manage().window().maximize();
            WebDriverWait wait = seleniumHandler.getDriverWait(driver, Duration.ofSeconds(10));

            seleniumHandler.handleCookieConsent(wait, By.xpath("//button[contains(., 'Consent')]"));

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table.table-hover tbody tr")));
            List<WebElement> rows = driver.findElements(By.cssSelector("table.table-hover tbody tr[data-symbol]"));

            for (WebElement row : rows) {
                try {
                    WebElement nameElement = row.findElement(By.cssSelector("td.datatable-item-first b"));
                    String name = nameElement.getText().trim();

                    WebElement unitElement = row.findElement(
                            By.cssSelector("td.datatable-item-first div[style*='font-size: 10px;']"));
                    String unitText = unitElement.getText().trim();

                    WebElement priceElement = row.findElement(By.cssSelector("td#p.datatable-item"));
                    String price = priceElement.getText().trim();

                    WebElement linkElement = row.findElement(By.cssSelector("td.datatable-item-first a"));
                    String href = linkElement.getAttribute("href");
                    String uri = href.substring(href.lastIndexOf("/") + 1);

                    CurrencyType currencyType = extractCurrency(unitText);
                    UnitType unitType = extractUnit(unitText);

                    if (currencyType != null && unitType != null) {
                        FinanceDetail detail = new FinanceDetail(
                                name,
                                null,
                                uri,
                                unitType.name(),
                                price,
                                currencyType.name(),
                                AssetType.COMMODITY.name(),
                                LocalDateTime.now()
                        );
                        commodities.put(uri, detail);
                    }
                } catch (NoSuchElementException e) {
                    log.warn("Skipping row due to missing elements: {}", e.getMessage());
                }
            }

             databaseUpdater.saveFinanceToDatabase(AssetType.COMMODITY.name(), commodities);
            log.info("Successfully processed {} commodities", commodities.size());
        } catch (Exception e) {
            throw new FinanceNotFoundException("TradingEconomics data: " + e.getMessage());
        } finally {
            if (driver != null) driver.quit();
            System.gc();
        }
    }
    private CurrencyType extractCurrency(String unit) {
        for (CurrencyType currency : CurrencyType.values()) {
            if (unit.toUpperCase().contains(currency.name())) return currency;
        }
        return null;
    }

    private UnitType extractUnit(String unit) {
        for (UnitType type : UnitType.values()) {
            if (unit.toUpperCase().contains(type.name())) return type;
        }
        return null;
    }
}
