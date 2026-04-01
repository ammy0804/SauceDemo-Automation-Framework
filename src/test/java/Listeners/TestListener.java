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
        if (test != null) {
            test.fail("Test Failed: " + result.getThrowable().getMessage());
            
            WebDriver driver = ((BaseTest) result.getInstance()).getDriver();
            if (driver != null) {
                String base64Screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                test.addScreenCaptureFromBase64String(base64Screenshot, "Failure Screenshot");
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (BaseTest.test.get() != null) {
            BaseTest.test.get().pass("Test Passed");
        }
    }
}