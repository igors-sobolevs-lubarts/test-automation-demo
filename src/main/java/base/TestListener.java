package base;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;
import utils.RestClientBase;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        // Only process failed tests
        if (testResult.getStatus() != ITestResult.FAILURE) return;

        Object instance = testResult.getInstance();
        String testName = testResult.getName();

        // Attach UI artifacts if applicable
        attachUiArtifacts(instance, testName);

        // Attach API artifacts if applicable
        attachApiArtifacts(instance);

        // Attach exception stacktrace
        Throwable throwable = testResult.getThrowable();
        if (throwable != null) {
            Allure.step("Attached Exception", () -> {
                Allure.addAttachment("Exception",
                        new ByteArrayInputStream(getStackTraceBytes(throwable)));
            });
        }
    }

    private void attachUiArtifacts(Object instance, String testName) {
        if (!(instance instanceof BaseWebTest)) return;

        try {
            WebDriver driver = ((BaseWebTest) instance).driver;
            if (driver != null) {
                Allure.step("Attached UI Artifacts", () -> {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    Allure.addAttachment(testName + " - Screenshot on failure",
                            new ByteArrayInputStream(screenshot));

                    byte[] pageSource = driver.getPageSource().getBytes(StandardCharsets.UTF_8);
                    Allure.addAttachment(testName + " - Page Source",
                            new ByteArrayInputStream(pageSource));
                });
            }
        } catch (Exception e) {
            Allure.addAttachment("UI attach error", e.getMessage());
        }
    }

    private void attachApiArtifacts(Object instance) {
        try {
            if (!(instance instanceof BaseApiTest)) return;

            BaseApiTest apiTest = (BaseApiTest) instance;
            RestClientBase client = apiTest.getClient();

            // Attach last request
            String method = client.getLastRequestMethod();
            String url = client.getLastRequestUrl();
            Map<String, ?> headers = client.getLastRequestHeaders();
            Object body = client.getLastRequestBody();

            if (method != null && url != null) {
                StringBuilder req = new StringBuilder();
                req.append("Method: ").append(method).append("\n")
                        .append("URL: ").append(url).append("\n");

                if (headers != null && !headers.isEmpty()) {
                    req.append("--- Headers ---\n").append(headers.toString()).append("\n");
                }
                if (body != null) {
                    req.append("--- Body ---\n").append(body).append("\n");
                }

                Allure.step("Attached API Request", () -> {
                    Allure.addAttachment("API Request", new ByteArrayInputStream(req.toString().getBytes(StandardCharsets.UTF_8)));
                });
            }

            // Attach last response
            if (client.getLastResponse() != null) {
                StringBuilder resp = new StringBuilder();
                resp.append("Status code: ").append(client.getLastResponse().getStatusCode()).append("\n");
                if (client.getLastResponse().getHeaders() != null)
                    resp.append("--- Headers ---\n").append(client.getLastResponse().getHeaders().toString()).append("\n");
                resp.append("--- Body ---\n").append(client.getLastResponse().getBody().asPrettyString()).append("\n");

                Allure.step("Attached API Response", () -> {
                    Allure.addAttachment("API Response", new ByteArrayInputStream(resp.toString().getBytes(StandardCharsets.UTF_8)));
                });
            }

        } catch (Exception e) {
            Allure.addAttachment("API attach error", e.getMessage());
        }
    }

    private byte[] getStackTraceBytes(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString()).append("\n");
        for (StackTraceElement el : t.getStackTrace()) {
            sb.append("\tat ").append(el.toString()).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void onStart(ITestContext iTestContext) {
        System.out.println("Starting: " + iTestContext.getName());
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        System.out.println("Finished: " + iTestContext.getName());
    }

}
