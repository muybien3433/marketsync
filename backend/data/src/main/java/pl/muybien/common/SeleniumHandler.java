package pl.muybien.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeleniumManager {
    private final static String REMOTE_WEB_DRIVER = "http://selenium-chrome:4444/wd/hub";

    public final WebDriver getDriver() {
        try {
            return new RemoteWebDriver(new URL(REMOTE_WEB_DRIVER), createChromeOptions());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public final WebDriverWait getDriverWait(WebDriver driver, Duration duration) {
        return new WebDriverWait(driver, duration);
    }

    public final void handleCookieConsent(WebDriver driver, WebDriverWait wait, String targetUrl) {
        try {
            driver.get(targetUrl);
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
