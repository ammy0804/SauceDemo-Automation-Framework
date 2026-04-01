package Base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import utils.ConfigReader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected WebDriver driver;
    protected static ExtentReports extent;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    protected static ThreadLocal<String> threadBrowser = new ThreadLocal<>();

    @BeforeSuite
    public void setupReport() {
        // Create directory using absolute path
        String reportFolderPath = System.getProperty("user.dir") + File.separator + "reports";
        File reportDir = new File(reportFolderPath);
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        String reportFilePath = reportFolderPath + File.separator + "SauceDemoReport.html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
        
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("SauceDemo Results");
        spark.config().setReportName("Standard Automation Suite");
        
        extent = new ExtentReports();
        extent.attachReporter(spark);
        
        extent.setSystemInfo("QA Engineer", "Amit Dehury");
        extent.setSystemInfo("Environment", "CI/CD Grid");
    }

    @Parameters({"browser", "environment"})
    @BeforeMethod
    public void setUp(Method method, @Optional("chrome") String browser, @Optional("local") String env) throws Exception {
        threadBrowser.set(browser.toUpperCase());
        test.set(extent.createTest(method.getName()));
        test.get().assignCategory(threadBrowser.get());

        // --- THE "ULTIMATE" CHROME FIX ---
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless=new");
        chromeOptions.addArguments("--window-size=1920,1080");
        
        // 1. Disable Password Breach & Leak Detection (The popup you saw)
        chromeOptions.addArguments("--disable-features=PasswordGeneration,PasswordLeakDetection,PasswordManagerHintSending");
        
        // 2. Disable FedCM (The new Chrome identity popup)
        chromeOptions.addArguments("--disable-features=FedCm"); 
        
        // 3. General automation stability
        chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
        chromeOptions.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        chromeOptions.setExperimentalOption("useAutomationExtension", false);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        chromeOptions.setExperimentalOption("prefs", prefs);

        if (env.equalsIgnoreCase("grid")) {
            URL gridUrl = new URL("http://localhost:4444/wd/hub");
            if (browser.equalsIgnoreCase("firefox")) {
                driver = new RemoteWebDriver(gridUrl, new FirefoxOptions());
            } else if (browser.equalsIgnoreCase("edge")) {
                driver = new RemoteWebDriver(gridUrl, new EdgeOptions());
            } else {
                driver = new RemoteWebDriver(gridUrl, chromeOptions);
            }
        } else {
            if (browser.equalsIgnoreCase("firefox")) {
                driver = new FirefoxDriver();
            } else if (browser.equalsIgnoreCase("edge")) {
                driver = new EdgeDriver();
            } else {
                driver = new ChromeDriver(chromeOptions);
            }
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(ConfigReader.getProperty("url")); 
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        test.remove();
        threadBrowser.remove();
    }

    @AfterSuite(alwaysRun = true)
    public void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}