package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException; // <-- Added
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select; // <-- Added
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;
import java.util.List; // <-- Added

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    // Constructor
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    }

    // --- 1. OPTIMIZED CLICK METHODS ---
    
    protected void clickElement(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void clickElement(By locator, int timeoutInSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        customWait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    // --- 2. BULLETPROOF TEXT ENTRY ---
    
    protected void enterText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE); 
        element.sendKeys(text);
    }

    // --- 3. SAFE BOOLEAN CHECKS ---
    
    protected String getElementText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) { // <-- OPTIMIZATION: Catches both!
            return false; 
        }
    }

    // --- 4. SMART DROPDOWN METHOD ---
    protected void selectFromDropdown(By locator, String visibleText) {
        WebElement dropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        // OPTIMIZATION: Much cleaner now that we added the import at the top!
        Select dropdown = new Select(dropdownElement); 
        dropdown.selectByVisibleText(visibleText);
    }

    // --- 5. SMART LIST CLICK METHOD ---
    protected void clickElementInList(By locator, int index) {
        // OPTIMIZATION: Safely waits for the entire list to load, preventing OutOfBounds errors!
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        elements.get(index).click();
    }
}