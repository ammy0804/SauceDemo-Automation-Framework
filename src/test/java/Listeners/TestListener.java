package Listeners;

import Base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = BaseTest.test.get();
        
        // 1. NULL CHECK: Ensure the test was actually created before trying to log to it
        if (test != null) {
            // Log the failure reason
            test.fail(result.getThrowable());

            try {
                // Grab the WebDriver from the specific test class
                Object testClass = result.getInstance();
                WebDriver driver = ((BaseTest) testClass).getDriver();

                // Take screenshot and attach to report
                if (driver != null) {
                    String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                    test.addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
                }
            } catch (Exception e) {
                // If the screenshot fails, log it to the report instead of just the console!
                test.info("Warning: Could not attach screenshot due to: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = BaseTest.test.get();
        if (test != null) {
            test.pass("Test Passed Successfully");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = BaseTest.test.get();
        if (test != null) {
            // 2. SAFE SKIP: Check if there is an actual error attached to the skip
            if (result.getThrowable() != null) {
                test.skip("Test Skipped: " + result.getThrowable().getMessage());
            } else {
                test.skip("Test Skipped (Likely due to a dependency failure).");
            }
        }
    }
}