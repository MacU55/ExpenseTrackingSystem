package systemTrackingExpence;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class AuthenticationInApp {

//    test to login to web app with Selenium webdriver

    @Test
    public void testLogin() {
        System.setProperty("webdriver.chrome.driver", "D:\\devTools\\chromedrivers\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://www.localhost:8080/login");
        String pageTitle = driver.getTitle();		//get the title of the webpage
        System.out.println("The title of this page is ===> " +pageTitle);
        Assert.assertEquals("Login form", pageTitle);	//verify the title of the webpage

        driver.findElement(By.name("email")).clear();//clear the input field before entering any value
        driver.findElement(By.name("email")).sendKeys("bob@mail.com");//enter the value of username
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("bob1975");//enter the value of password
        driver.findElement(By.name("Submit")).click();		//click Login button
        System.out.println("Successfully logged in");
//        driver.quit();

    }
}
