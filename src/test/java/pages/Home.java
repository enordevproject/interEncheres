package pages;

/*******************************************************************************************
 * Page Factory class for Interencheres Home Page
 * @author Your Name
 *******************************************************************************************/

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import base.PageBase;


public class Home extends PageBase {

    public Home(WebDriver driver) {
        super(driver);
    }

    /*******************************************************************************************
     * All WebElements are identified by @FindBy annotation
     *******************************************************************************************/

    // Locators
    private By loginButton = By.xpath("//div[@class='ml-1']");


    /*******************************************************************************************
     * All Methods for performing actions
     *******************************************************************************************/

    public void clickLoginButton() {
        findElement(loginButton).click();
    }

}
