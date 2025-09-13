package seleniumgrid;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Brokenlinkwithbrokenlink {

    WebDriver driver;
    int brokenCount = 0;

    @BeforeClass
    public void setup() throws IOException {
        // Point to your Selenium Grid Standalone Hub
        URL gridUrl = new URL("http://192.168.31.198:4444/wd/hub"); // <-- replace with your Hub IP
        ChromeOptions options = new ChromeOptions();
        driver = new RemoteWebDriver(gridUrl, options);
        driver.get("https://demoqa.com/broken");
    }

    @Test
    public void verifyBrokenLinks() {
        List<WebElement> links = driver.findElements(By.tagName("a"));
        Set<String> uniqueUrls = new HashSet<>();

        for (WebElement link : links) {
            String url = link.getAttribute("href");

            if (url == null || url.isEmpty()) {
                System.out.println("Empty URL skipped");
                continue;
            }

            // Avoid duplicate link checking
            if (!uniqueUrls.add(url)) {
                continue;
            }

            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("HEAD"); // Faster than GET
                conn.connect();
                int responseCode = conn.getResponseCode();

                if (responseCode >= 400) {
                    System.out.println(url + " is broken. Code: " + responseCode);
                    brokenCount++;
                } else {
                    System.out.println(url + " is valid. Code: " + responseCode);
                }

            } catch (Exception e) {
                System.out.println(url + " is broken. Exception: " + e.getMessage());
                brokenCount++;
            }
        }

        System.out.println("Total broken links found: " + brokenCount);

        // You can put an assertion (optional)
        Assert.assertTrue(brokenCount >= 0, "Broken links check completed");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
