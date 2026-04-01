package Base;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.Eyes;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.epam.healenium.SelfHealingDriver; // <-- ADDED HEALENIUM IMPORT
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.*;
import utils.ConfigReader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected WebDriver driver;
    
    protected static ExtentReports extent;
    public static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    protected static ThreadLocal<String> threadBrowser = new ThreadLocal<>();
    protected static ThreadLocal<Eyes> eyes = new ThreadLocal<>();

    @BeforeSuite
    public void setupReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("reports/SauceDemoReport.html");
        
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("SauceDemo Tests");
        spark.config().setReportName("Automation Results");
        
        extent = new ExtentReports();
        extent.attachReporter(spark);
        
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Execution Mode", "Docker Grid - Cross Browser");
        extent.setSystemInfo("QA Engineer", ConfigReader.getProperty("qa_engineer")); 
    }

    @SuppressWarnings("deprecation")
    @Parameters({"browser", "environment"})
    @BeforeMethod
    public void setUp(Method method, @Optional("config") String browser, @Optional("local") String env) throws Exception {
        
        if (browser.equalsIgnoreCase("config")) {
            browser = ConfigReader.getProperty("browser");
        }

        threadBrowser.set(browser.toUpperCase());
        test.set(extent.createTest(method.getName()));
        test.get().assignCategory(threadBrowser.get());

        // --- HEALENIUM STEP 1: Define a delegate driver ---
        WebDriver delegate = null;
        URL gridUrl = new URL("http://localhost:4444/wd/hub");

        if (env.equalsIgnoreCase("grid")) {
            switch (browser.toLowerCase()) {
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--headless=new");
                    delegate = new RemoteWebDriver(gridUrl, edgeOptions);
                    break;
                case "firefox":
                    FirefoxOptions fireOptions = new FirefoxOptions();
                    fireOptions.addArguments("-headless");
                    delegate = new RemoteWebDriver(gridUrl, fireOptions);
                    break;
                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--headless=new");
                    delegate = new RemoteWebDriver(gridUrl, chromeOptions);
                    break;
            }
        } else {
            switch (browser.toLowerCase()) {
                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("-headless"); 
                    delegate = new FirefoxDriver(firefoxOptions);
                    break;
                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--headless=new"); 
                    delegate = new EdgeDriver(edgeOptions);
                    break;
                case "safari":
                    delegate = new SafariDriver(new SafariOptions());
                    break;
                case "chrome":
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--headless=new", "--window-size=1920,1080", "--incognito");
                    
                    Map<String, Object> prefs = new HashMap<>();
                    prefs.put("credentials_enable_service", false);
                    prefs.put("profile.password_manager_enabled", false);
                    chromeOptions.setExperimentalOption("prefs", prefs);
                    
                    delegate = new ChromeDriver(chromeOptions);
                    break;
            }
        }
        
        // --- HEALENIUM STEP 2: Wrap the delegate driver ---
        // This activates the AI self-healing proxy
        this.driver = SelfHealingDriver.create(delegate);
        
     // --- 4. NEW: Initialize Applitools ---
        Eyes threadEyes = new Eyes();
        threadEyes.setApiKey(ConfigReader.getProperty("applitools_api_key"));
        eyes.set(threadEyes);
        
     // Start the visual test session
        eyes.get().open(driver, "SauceDemo App", method.getName(), new RectangleSize(1280, 800));
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.get(ConfigReader.getProperty("url"));
        
        
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        if(browser.equalsIgnoreCase("safari") && !env.equalsIgnoreCase("grid")) {
            driver.manage().window().maximize(); 
        }
        
        driver.get(ConfigReader.getProperty("url")); 
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        if (extent != null) {
            extent.flush(); 
        }
        test.remove();
        threadBrowser.remove();
            try {
                // --- NEW: Close Applitools Test ---
                if (eyes.get() != null && eyes.get().getIsOpen()) {
                    eyes.get().close(); 
                }
            } finally {
                // Existing Cleanup
                if (driver != null) { driver.quit(); }
                if (extent != null) { extent.flush(); }
                test.remove();
                threadBrowser.remove();
                
                // Clean up Eyes thread
                if (eyes.get() != null) {
                    eyes.get().abortIfNotClosed();
                    eyes.remove();
                }
            }
        }

        

    @AfterSuite
    public void tearDownReport() throws Exception {
        if (extent != null) {
            extent.flush(); 
        }
        
        File reportFile = new File("reports/SauceDemoReport.html");
        if(reportFile.exists()) {
            // Check if Desktop is supported to avoid Jenkins crashes
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(reportFile.toURI());
            }
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}