package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends BasePage {
    
    // --- LOCATORS ---
    private By menuBtn = By.id("react-burger-menu-btn");
    private By aboutLink = By.id("about_sidebar_link");
    private By logoutLink = By.id("logout_sidebar_link");
    private By resetLink = By.id("reset_sidebar_link");
    private By cartIcon = By.className("shopping_cart_link");
    private By footer = By.className("footer_copy");
    private By sortDropdown = By.className("product_sort_container");
    private By addButtons = By.xpath("//button[text()='Add to cart']");
    private By removeButtons = By.xpath("//button[text()='Remove']");

    // --- CONSTRUCTOR ---
    public InventoryPage(WebDriver driver) {
        super(driver); 
    }

    // --- OPTIMIZED SMART CLICK METHODS ---
    public void clickMenu() { clickElement(menuBtn); }
    public void clickAbout() { clickElement(aboutLink); }
    public void clickLogout() { clickElement(logoutLink); }
    public void clickReset() { clickElement(resetLink); }
    public void goToCart() { clickElement(cartIcon); }

    // --- OPTIMIZED SMART TEXT METHODS ---
    public String getFooterText() { return getElementText(footer); }
    public String getCartCount() { return getElementText(cartIcon); }
    public boolean isFooterDisplayed() { return isElementDisplayed(footer); }

    // --- NEW: OPTIMIZED SMART LIST & DROPDOWN METHODS ---
    
    public void addProductByIndex(int index) { 
        // Now safely waits for the products to load before clicking!
        clickElementInList(addButtons, index); 
    }
    
    public void sortItems(String visibleText) {
        // Now safely waits for the dropdown to be interactable!
        selectFromDropdown(sortDropdown, visibleText);
    }
    
    public int getRemoveButtonCount() { 
        // For simple sizing without clicking, raw findElements is usually safe enough 
        // if we assume the page has already loaded, but you could wrap this too!
        return driver.findElements(removeButtons).size(); 
    }
}