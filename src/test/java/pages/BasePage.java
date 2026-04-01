package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    }

    // --- 1. STANDARD CLICK WITH JS FALLBACK ---
    protected void clickElement(By locator) {
        try {
            // Try standard click first
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            // FALLBACK: Use JavaScript to click if blocked by a popup
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // --- 2. LIST CLICK WITH JS FALLBACK (Resolves InventoryPage Error) ---
    protected void clickElementInList(By locator, int index) {
        // Wait for all elements in the list to be present
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        try {
            elements.get(index).click();
        } catch (Exception e) {
            // FALLBACK: Use JavaScript for the specific item in the list
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elements.get(index));
        }
    }

    // --- 3. BULLETPROOF TEXT ENTRY ---
    protected void enterText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        // Use CTRL+A and Delete for a cleaner field reset than .clear()
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE); 
        element.sendKeys(text);
    }

    // --- 4. SAFE UTILITY METHODS ---
    protected String getElementText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false; 
        }
    }

    protected void selectFromDropdown(By locator, String visibleText) {
        WebElement dropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(dropdownElement).selectByVisibleText(visibleText);
    }
}