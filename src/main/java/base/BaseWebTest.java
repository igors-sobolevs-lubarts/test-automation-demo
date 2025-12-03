package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import utils.DriverFactory;

@Listeners({base.TestListener.class})
public class BaseWebTest {

    protected WebDriver driver;
    protected String webBaseUrl;

    @BeforeMethod
    public void setupBrowser() {
        webBaseUrl = System.getenv("WEB_BASE_URL");
        if (webBaseUrl == null || webBaseUrl.isEmpty()) {
            webBaseUrl = System.getProperty("WEB_BASE_URL");
        }
        if (webBaseUrl == null || webBaseUrl.isEmpty()) {
            throw new RuntimeException("WEB_BASE_URL environment variable or system property is missing");
        }

        driver = DriverFactory.createDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }
}
