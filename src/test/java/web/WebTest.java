package web;

import base.BaseWebTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WebTest extends BaseWebTest {

    @Test
    public void webPageOpenTest() {
        driver.get(webBaseUrl);
        String pageTitle = driver.getTitle();
        Assert.assertTrue(pageTitle.contains("Google"), "Expected page title to contain 'Google', but was: " + pageTitle);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.startsWith("https://"), "Expected a valid https URL, but got: " + currentUrl);
    }

    @Test
    public void webSearchMadeToFailTest() {
        driver.get(webBaseUrl);
        driver.findElement(By.name("q")).sendKeys("TestNG\n");
        Assert.assertFalse(driver.getPageSource().contains("TestNG"));
    }
}
