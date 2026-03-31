package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartAndCheckoutPages {
    WebDriver driver;
    // Cart Locators
    By removeBtn = By.xpath("//button[text()='Remove']");
    By continueShoppingBtn = By.id("continue-shopping");
    By checkoutBtn = By.id("checkout");
    
    // Checkout Info Locators
    By firstName = By.id("first-name");
    By lastName = By.id("last-name");
    By postalCode = By.id("postal-code");
    By continueBtn = By.id("continue");
    By cancelBtn = By.id("cancel");
    By errorMsg = By.cssSelector("h3[data-test='error']");
    
    // Checkout Overview Locators
    By totalLabel = By.className("summary_total_label");
    By finishBtn = By.id("finish");
    By completeHeader = By.className("complete-header");

    public CartAndCheckoutPages(WebDriver driver) { this.driver = driver; }

    public void removeFirstItem() { driver.findElement(removeBtn).click(); }
    public void clickContinueShopping() { driver.findElement(continueShoppingBtn).click(); }
    public void clickCheckout() { driver.findElement(checkoutBtn).click(); }
    
    public void fillCheckoutDetails(String fName, String lName, String zip) {
        driver.findElement(firstName).sendKeys(fName);
        driver.findElement(lastName).sendKeys(lName);
        driver.findElement(postalCode).sendKeys(zip);
    }
    public void clickContinue() { driver.findElement(continueBtn).click(); }
    public void clickCancel() { driver.findElement(cancelBtn).click(); }
    public String getCheckoutError() { return driver.findElement(errorMsg).getText(); }
    
    public String getTotalPrice() { return driver.findElement(totalLabel).getText(); }
    public void clickFinish() { driver.findElement(finishBtn).click(); }
    public String getCompleteMessage() { return driver.findElement(completeHeader).getText(); }
}