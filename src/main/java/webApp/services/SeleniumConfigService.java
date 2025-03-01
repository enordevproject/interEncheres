package webApp.services;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import selenium.base.BasePage;

@Service
public class SeleniumConfigService extends BasePage {

    public SeleniumConfigService() {
        super(); // Call BasePage constructor to ensure proper initialization
    }

    /**
     * Retrieves the WebDriver instance. If not initialized, it sets up the driver.
     *
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        if (driver == null) {
            initDriver(); // Use BasePage's driver initialization
        }
        return driver;
    }

    /**
     * Closes and quits the WebDriver instance.
     */
    public void closeDriver() {
        quitDriver(); // Use BasePage's method to properly close the WebDriver
    }
}
