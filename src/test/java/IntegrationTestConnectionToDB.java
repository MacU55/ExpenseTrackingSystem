import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;


public class IntegrationTestConnectionToDB {

//    Test to connect database "exptracksystemtest", create record and check it.
//    Test should be managed by Selenium webdriver.

    @Test
    public void testCreateNewExpense() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "D:\\devTools\\chromedrivers\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("http://www.localhost:8080/login");
        String pageTitle = driver.getTitle();		//get the title of the webpage
        System.out.println("The title of this page is ===> " +pageTitle);
        Assert.assertEquals("Login form", pageTitle);	//verify the title of the webpage

        driver.findElement(By.name("email")).clear();//clear the input field before entering any value
        driver.findElement(By.name("email")).sendKeys("bob@mail.com");//enter user's email
        Thread.sleep(1000);
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys("bob1975");//enter the value of password
        Thread.sleep(1000);
        driver.findElement(By.name("Submit")).click();	//click Login button
        Thread.sleep(1000);
        driver.findElement(By.name("createNewExpense")).click(); //click to forward to page edit_expense.html
        Thread.sleep(1000);
        driver.findElement(By.name("description")).sendKeys("bussiness trip");
        Thread.sleep(1000);
        driver.findElement(By.name("expenseDate")).sendKeys("05/05/2021");
        Thread.sleep(1000);
        driver.findElement(By.name("amount")).sendKeys("15.38");
        Thread.sleep(1000);
         WebElement searchBox = driver.findElement(By.name("expenseType"));
         dropDownSelectByText(searchBox, "Education");
        Thread.sleep(1000);
        driver.findElement(By.name("save")).click();




        System.out.println("Successfully logged in");
//        driver.quit();

    }

    public static void dropDownSelectByText (WebElement webElement, String VisibleText){
        Select selObj=new Select(webElement);
        selObj.selectByVisibleText(VisibleText);
    }
}
