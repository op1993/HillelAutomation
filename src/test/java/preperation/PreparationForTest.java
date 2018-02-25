package preperation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Created by Oleh on 18-Feb-18.
 */
public class PreparationForTest {
    public  WebDriver driver;
    public  WebDriverWait wait;

    @BeforeClass
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeMethod
    public  void setupBrowser(){
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait= new WebDriverWait(driver, 15);
    }

    @AfterMethod
    public void close(){
        driver.close();
    }



}
