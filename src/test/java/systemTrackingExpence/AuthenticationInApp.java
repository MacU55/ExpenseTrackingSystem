package systemTrackingExpence;

import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class AuthenticationInApp {

    @Test
    public void login() {
        System.setProperty("webdriver.chrome.driver", "D:\\devTools\\chromedrivers\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://www.localhost:8080/");

    }
}
