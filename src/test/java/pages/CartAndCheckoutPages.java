package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

// 1. EXTEND BASE PAGE
public class CartAndCheckoutPages extends BasePage { 
    
    // 2. MAKE ALL LOCATORS PRIVATE
    // Cart Locators
    private By removeBtn = By.xpath("//button[text()='Remove']");
    private By continueShoppingBtn = By.id("continue-shopping");
    private By checkoutBtn = By.id("checkout");
    
    // Checkout Info Locators
    private By firstName = By.id("first-name");
    private By lastName = By.id("last-name");
    private By postalCode = By.id("postal-code");
    private By continueBtn = By.id("continue");
    private By cancelBtn = By.id("cancel");
    private By errorMsg = By.cssSelector("h3[data-test='error']");
    
    // Checkout Overview Locators
    private By totalLabel = By.className("summary_total_label");
    private By finishBtn = By.id("finish");
    private By completeHeader = By.className("complete-header");

    // 3. PASS DRIVER TO SUPERCLASS
    public CartAndCheckoutPages(WebDriver driver) { 
        super(driver); 
    }

    // --- 4. OPTIMIZED SMART CLICK METHODS ---
    public void removeFirstItem() { clickElement(removeBtn); }
    public void clickContinueShopping() { clickElement(continueShoppingBtn); }
    public void clickCheckout() { clickElement(checkoutBtn); }
    public void clickContinue() { clickElement(continueBtn); }
    public void clickCancel() { clickElement(cancelBtn); }
    public void clickFinish() { clickElement(finishBtn); }
    
    // --- OPTIMIZED SMART TEXT ENTRY ---
    public void fillCheckoutDetails(String fName, String lName, String zip) {
        enterText(firstName, fName);
        enterText(lastName, lName);
        enterText(postalCode, zip);
    }
    
    // --- OPTIMIZED SMART TEXT RETRIEVAL ---
    public String getCheckoutError() { return getElementText(errorMsg); }
    public String getTotalPrice() { return getElementText(totalLabel); }
    public String getCompleteMessage() { return getElementText(completeHeader); }
}