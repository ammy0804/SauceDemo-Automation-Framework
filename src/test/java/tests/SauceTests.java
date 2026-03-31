package tests;

import Base.BaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartAndCheckoutPages;
import pages.InventoryPage;
import pages.LoginPage;
import utils.ConfigReader;

public class SauceTests extends BaseTest {

    // 1. Successful login
    @Test(priority = 1)
    public void testSuccessfulLogin() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"));
        
        // Optional: Manual logging step
        test.get().pass("Login was successful");
    }

    // 2. Unsuccessful login
    @Test(priority = 2)
    public void testUnsuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("locked_out_user", "secret_sauce");
        String error = loginPage.getErrorMessage();
        System.out.println("Login Error Output: " + error);
        Assert.assertEquals(error, "Epic sadface: Sorry, this user has been locked out.");
    }

    // 3. Happy path booking 2 products
    @Test(priority = 3)
    public void testHappyPathPurchase() {
        new LoginPage(driver).login(
            ConfigReader.getProperty("username"), 
            ConfigReader.getProperty("password")
        );
        
        InventoryPage inventory = new InventoryPage(driver);
        inventory.addProductByIndex(0);
        inventory.addProductByIndex(1);
        inventory.goToCart();
        
        CartAndCheckoutPages checkout = new CartAndCheckoutPages(driver);
        checkout.clickCheckout();
        checkout.fillCheckoutDetails(
            ConfigReader.getProperty("firstname"),
            ConfigReader.getProperty("lastname"),
            ConfigReader.getProperty("zip")
        );
        checkout.clickContinue();
        System.out.println("Total Price: " + checkout.getTotalPrice());
        checkout.clickFinish();
        
        Assert.assertEquals(checkout.getCompleteMessage(), "Thank you for your order!");
    }

    // 4. Verify footer
    @Test(priority = 4)
    public void testFooter() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        InventoryPage inventory = new InventoryPage(driver);
        Assert.assertTrue(inventory.isFooterDisplayed());
        System.out.println("Footer Text: " + inventory.getFooterText());
    }

    // 5. Hamburger -> About
    @Test(priority = 5)
    public void testAboutNavigation() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        InventoryPage inventory = new InventoryPage(driver);
        inventory.clickMenu();
        inventory.clickAbout(); // BasePage handles the wait automatically!
        Assert.assertEquals(driver.getCurrentUrl(), "https://saucelabs.com/");
    }

    // 6. Hamburger -> Logout
    @Test(priority = 6)
    public void testLogout() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        InventoryPage inventory = new InventoryPage(driver);
        inventory.clickMenu();
        inventory.clickLogout(); // BasePage handles the wait automatically!
        Assert.assertTrue(driver.getCurrentUrl().equals("https://www.saucedemo.com/"));
    }

    // 7a. Reset App State
    @Test(priority = 7)
    public void testResetAppState() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        InventoryPage inventory = new InventoryPage(driver);
        inventory.addProductByIndex(0);
        inventory.addProductByIndex(1);
        inventory.clickMenu();
        inventory.clickReset(); // BasePage handles the wait automatically!
        driver.navigate().refresh(); 
        Assert.assertEquals(inventory.getRemoveButtonCount(), 0, "Remove buttons should revert to Add");
    }

    // 7b. Remove from Cart & Continue Shopping
    @Test(priority = 8)
    public void testRemoveAndContinueShopping() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        InventoryPage inventory = new InventoryPage(driver);
        inventory.addProductByIndex(0);
        inventory.goToCart();
        CartAndCheckoutPages cart = new CartAndCheckoutPages(driver);
        cart.removeFirstItem();
        cart.clickContinueShopping();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"));
    }

    // 8. Checkout without details error
    @Test(priority = 9)
    public void testCheckoutEmptyDetailsError() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        new InventoryPage(driver).goToCart();
        CartAndCheckoutPages checkout = new CartAndCheckoutPages(driver);
        checkout.clickCheckout();
        checkout.clickContinue();
        String error = checkout.getCheckoutError();
        System.out.println("Checkout Error Output: " + error);
        Assert.assertEquals(error, "Error: First Name is required");
    }

    // 9. Checkout Cancel
    @Test(priority = 10)
    public void testCheckoutCancel() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        new InventoryPage(driver).goToCart();
        CartAndCheckoutPages checkout = new CartAndCheckoutPages(driver);
        checkout.clickCheckout();
        checkout.fillCheckoutDetails("John", "Doe", "12345");
        checkout.clickCancel();
        Assert.assertTrue(driver.getCurrentUrl().contains("cart.html"));
    }

    // 10. Sort Z-A and Buy 3rd item
    @Test(priority = 11)
    public void testSortAndBuy() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        InventoryPage inventory = new InventoryPage(driver);
        inventory.sortItems("Name (Z to A)");
        inventory.addProductByIndex(2); 
        inventory.goToCart();
        CartAndCheckoutPages checkout = new CartAndCheckoutPages(driver);
        checkout.clickCheckout();
        checkout.fillCheckoutDetails("John", "Doe", "12345");
        checkout.clickContinue();
        checkout.clickFinish();
        Assert.assertEquals(checkout.getCompleteMessage(), "Thank you for your order!");
    }

    // 11. INTENTIONAL FAILED TESTS FOR REPORTING

    @Test(priority = 12)
    public void testFailWrongLoginTitle() {
        Assert.assertEquals(driver.getTitle(), "Google", "Intentional Failure: Title mismatch");
    }

    @Test(priority = 13)
    public void testFailElementNotFound() {
        new LoginPage(driver).login("standard_user", "secret_sauce");
        driver.findElement(By.id("fake-element-id")).click(); 
    }
}