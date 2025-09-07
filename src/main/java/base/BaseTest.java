package base;

import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import utility.ConfigReader;

public final class BaseTest {
    private static final ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();
    private static volatile Properties prop;

    private BaseTest() {}

    /** Load config-{env}.properties only once per JVM */
    public static void loadConfig() {
        if (prop == null) {
            synchronized (BaseTest.class) {
                if (prop == null) {
                    String env = System.getProperty("env", "qa"); // default qa
                    prop = ConfigReader.loadProperties(env);
                    System.out.println("✅ Config loaded for env: " + env);
                }
            }
        }
    }

    /**
     * Create a driver for the current thread if it doesn't exist.
     * Should be called in a Cucumber @Before hook (per scenario).
     */
    public static void createDriverIfNeeded() {
        if (driver.get() != null) return;

        loadConfig();
        String browser = System.getProperty("browser", prop.getProperty("browser", "chrome"));
        System.out.println("✅ Browser: " + browser);

        switch (browser.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver());
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver.set(new FirefoxDriver());
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver.set(new EdgeDriver());
                break;
            default:
                throw new RuntimeException("❌ Unsupported browser: " + browser);
        }

        WebDriver wd = getDriver();
        wd.manage().window().maximize();
        wd.manage().deleteAllCookies();

        int implicitWait = Integer.parseInt(prop.getProperty("implicitWait", "10"));
        wd.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

        String baseURL = prop.getProperty("baseURL");
        if (baseURL != null && !baseURL.isEmpty()) {
            wd.get(baseURL);
        }

        System.out.println("🟢 Driver created on thread: " + Thread.currentThread().getId());
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static void quitDriver() {
        RemoteWebDriver wd = driver.get();
        if (wd != null) {
            try {
                wd.quit();
            } catch (Exception ignored) {}
            driver.remove();
            System.out.println("🔚 Driver closed for thread: " + Thread.currentThread().getId());
        }
    }

    public static Properties getProps() {
        loadConfig();
        return prop;
    }
}
