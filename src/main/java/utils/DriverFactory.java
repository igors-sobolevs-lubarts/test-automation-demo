package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    public static WebDriver createDriver() {
        String browser = System.getenv("BROWSER");
        if (browser == null || browser.isEmpty()) {
            browser = System.getProperty("BROWSER", "chrome");
        }
        if ("chrome".equalsIgnoreCase(browser)) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            // Essential flags for Docker / headless execution
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-software-rasterizer");
            options.addArguments("--remote-debugging-port=9222");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");

            return new ChromeDriver(options);
        }

        throw new RuntimeException("Unsupported browser: " + browser);
    }
}
