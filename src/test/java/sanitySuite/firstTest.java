package sanitySuite;

import base.TestBase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.Home;

public class firstTest extends TestBase {

    private Home homePage;

    @BeforeClass
    public void setUp() {
        // Initialize the driver using TestBase



        // Initialize the Home page object
        homePage = new Home(driver);
    }

    @Test(priority = 1, description = "Open page, click on login, and perform login")
    public void openPageAndLogin() {
        log.info("Navigating to Interencheres home page.");
        homePage.performLogin("test@test.com","tesdd");






    }
}