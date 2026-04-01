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

public class BaseTest {
    protected WebDriver driver;
    protected static ExtentReports extent;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    protected static ThreadLocal<String> threadBrowser = new ThreadLocal<>();

    @BeforeSuite
    public void setupReport() {
        // 1. Create absolute path for the reports folder
        String reportFolderPath = System.getProperty("user.dir") + File.separator + "reports";
        File reportDir = new File(reportFolderPath);
        
        // 2. Automatically create the directory if it doesn't exist
        if (!reportDir.exists()) {
            reportDir.mkdirs();
        }

        // 3. Define report file path
        String reportFilePath = reportFolderPath + File.separator + "SauceDemoReport.html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportFilePath);
        
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("SauceDemo Grid Execution");
        spark.config().setReportName("Standard Automation Results");
        
        extent = new ExtentReports();
        extent.attachReporter(spark);
        
        // Custom Environment Info
        extent.setSystemInfo("QA Engineer", "Amit Dehury");
        extent.setSystemInfo("Execution Mode", "Selenium Grid 4");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
    }

    @Parameters({"browser", "environment"})
    @BeforeMethod
    public void setUp(Method method, @Optional("chrome") String browser, @Optional("local") String env) throws Exception {
        threadBrowser.set(browser.toUpperCase());
        test.set(extent.createTest(method.getName()));
        test.get().assignCategory(threadBrowser.get());

        if (env.equalsIgnoreCase("grid")) {
            // URL of your Docker Selenium Hub
            @SuppressWarnings("deprecation")
			URL gridUrl = new URL("http://localhost:4444/wd/hub");
            
            if (browser.equalsIgnoreCase("firefox")) {
                FirefoxOptions fireOptions = new FirefoxOptions();
                driver = new RemoteWebDriver(gridUrl, fireOptions);
            } else if (browser.equalsIgnoreCase("edge")) {
                EdgeOptions edgeOptions = new EdgeOptions();
                driver = new RemoteWebDriver(gridUrl, edgeOptions);
            } else {
                ChromeOptions chromeOptions = new ChromeOptions();
                driver = new RemoteWebDriver(gridUrl, chromeOptions);
            }
        } else {
            // Local Execution
            if (browser.equalsIgnoreCase("firefox")) {
                driver = new FirefoxDriver();
            } else if (browser.equalsIgnoreCase("edge")) {
                driver = new EdgeDriver();
            } else {
                driver = new ChromeDriver();
            }
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(ConfigReader.getProperty("url")); 
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        // Important: Cleanup ThreadLocal to prevent memory leaks
        test.remove();
        threadBrowser.remove();
    }

    @AfterSuite(alwaysRun = true)
    public void flushReport() {
        if (extent != null) {
            extent.flush();
            System.out.println(">>> SUCCESS: Extent Report generated at: " + System.getProperty("user.dir") + File.separator + "reports");
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}