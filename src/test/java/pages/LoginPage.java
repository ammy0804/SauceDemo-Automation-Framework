package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

// 1. Extend the BasePage
public class LoginPage extends BasePage {
    
    // 2. Make all locators strictly private
    private By username = By.id("user-name");
    private By password = By.id("password");
    private By loginBtn = By.id("login-button");
    private By errorMsg = By.cssSelector("h3[data-test='error']");

    // 3. Pass the driver up to BasePage to initialize the Smart Waits
    public LoginPage(WebDriver driver) { 
        super(driver); 
    }

    // --- 4. OPTIMIZED SMART METHODS ---
    
    public void login(String user, String pass) {
        // enterText safely waits for the field, clears it, and types the text!
        enterText(username, user);
        enterText(password, pass);
        clickElement(loginBtn);
    }
    
    public String getErrorMessage() { 
        // getElementText safely waits for the error to be visible before grabbing it
        return getElementText(errorMsg); 
    }
}