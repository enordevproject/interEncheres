package sanitySuite;

import org.testng.annotations.Test;

import base.TestBase;
import pages.Home;


public class firstTest extends TestBase{



    // Create an instance of the Home page class
    Home homePage = new Home(driver);


        @Test(priority = 1, description = "Open page and click on login")
        public void openPageAndClickLogin() {
            log.info("Open Google Search URL.");
            driver.get(data.getProperty("base.url"));  // Opening the base URL



            // Click on the login button
            homePage.clickLoginButton();

            log.info("Clicked on the login button.");
        }
}
