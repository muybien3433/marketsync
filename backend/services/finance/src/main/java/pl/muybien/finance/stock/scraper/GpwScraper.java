package pl.muybien.finance.stock.scraper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.exception.FinanceNotFoundException;
import pl.muybien.finance.*;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class GpwScraper {

    private final static String TARGET_URL = "https://gpwglobalconnect.pl/notowania";
    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";

    private final FinanceUpdater financeUpdater;

    @Async
    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(fixedRateString = "${gpw.update-rate-ms}")
    public void updateAvailableFinanceList() {
        LinkedHashSet<FinanceDetail> stocks = new LinkedHashSet<>();

        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            driver = new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), options);
            driver.get(TARGET_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            try {
                WebElement acceptCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Akceptuję')]")));
                acceptCookiesButton.click();
                log.info("Accepted cookie policy");
            } catch (Exception e) {
                log.info("Cookie acceptance button not found or already accepted");
            }

            WebElement body = driver.findElement(By.tagName("body"));
            String pageContent = body.getText();

            int indexOfFirst = pageContent.indexOf("AD");
            int indexOfLast = pageContent.lastIndexOf("Razem");
            if (indexOfFirst != -1 && indexOfLast != -1) {
                pageContent = pageContent.substring(indexOfFirst, indexOfLast);
            }

            String regex =
                    "([A-Za-z]+)\\s"
                            + "([A-Za-z]+)\\s"
                            + "([0-9]{2}:[0-9]{2}:[0-9]{2}|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)\\s"
                            + "([0-9,]+|-)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(pageContent);

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                String symbol = matcher.group(2).trim();
                String renewalRate = matcher.group(4).trim().replace(",", ".");
                String lastTransactionRate = matcher.group(9).trim().replace(",", ".");
                BigDecimal price = lastTransactionRate.equals("-") ?
                        new BigDecimal(renewalRate) : new BigDecimal(lastTransactionRate);

                var finance = FinanceDetail.builder()
                        .name(name)
                        .symbol(symbol)
                        .uri(name.toLowerCase())
                        .price(price)
                        .currency(CurrencyType.PLN)
                        .assetType(AssetType.STOCKS)
                        .build();

                stocks.add(finance);
            }
            financeUpdater.sortAndSaveFinanceToDatabase(AssetType.STOCKS.name(), stocks);
        } catch (MalformedURLException e) {
            throw new FinanceNotFoundException("Gpw data not found");
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        log.info("Finished updating available gpw list");
    }
}