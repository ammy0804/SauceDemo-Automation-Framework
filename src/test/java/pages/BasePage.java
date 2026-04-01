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

    // --- 1. OPTIMIZED CLICK WITH JS FALLBACK ---
    
    protected void clickElement(By locator) {
        try {
            // Try standard Selenium click first
            wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            // FALLBACK: Use JavaScript click if blocked by popups
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void clickElement(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            customWait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        } catch (Exception e) {
            // FALLBACK: JavaScript click
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    // --- 2. BULLETPROOF TEXT ENTRY ---
    
    protected void enterText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        // Clears text reliably before typing
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE); 
        element.sendKeys(text);
    }

    // --- 3. SAFE BOOLEAN & TEXT CHECKS ---
    
    protected String getElementText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    protected boolean isElementDisplayed(By locator) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            return shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false; 
        }
    }

    // --- 4. SMART DROPDOWN METHOD ---
    
    protected void selectFromDropdown(By locator, String visibleText) {
        WebElement dropdownElement = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        Select dropdown = new Select(dropdownElement); 
        dropdown.selectByVisibleText(visibleText);
    }

    // --- 5. SMART LIST CLICK METHOD ---
    
    protected void clickElementInList(By locator, int index) {
        List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        try {
            elements.get(index).click();
        } catch (Exception e) {
            // JS Fallback for list items as well
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elements.get(index));
        }
    }
}