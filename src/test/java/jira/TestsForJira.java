package jira;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import preperation.PreparationForTest;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static utils.CheckHashFile.generateHashForFileOfType;
import static utils.HashType.MD5;
import static utils.HashType.SHA1;


/**
 * Created by Oleh on 18-Feb-18.
 */
public class TestsForJira extends PreparationForTest {
    private String username = "autorob";
    private String password = "forautotests";

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\Oleh\\Desktop\\65fa961e-8f22-4fe6-a420-3c3c26dd2953.jpg._CB289161999__SL300__.jpg");
        String before = generateHashForFileOfType(file,MD5);
        System.out.println(before);

        File file2 = new File("D:\\Downloads TEMP\\65fa961e-8f22-4fe6-a420-3c3c26dd2953.jpg._CB289161999__SL300__.jpg");
        String after = generateHashForFileOfType(file2,MD5);
        System.out.println(after);
        System.out.println(before.equals(after));
    }

    private void authorization(String username, String password) {
        driver.get("http://jira.hillel.it:8080/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-form-username")));
        driver.findElement(By.id("login-form-username")).sendKeys(username);
        driver.findElement(By.id("login-form-password")).sendKeys(password);
        driver.findElement(By.id("login")).click();
    }

    private void createTask() throws InterruptedException {
        driver.findElement(By.id("create_link")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("create-issue-dialog")));
        driver.findElement(By.id("summary")).sendKeys("Oleh test");
    }

    private void uploadAttachment(WebDriver driver, String path) {
        WebElement dropZone = driver.findElement(By.cssSelector(".issue-drop-zone__target"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", dropZone);
        driver.findElement(By.cssSelector(".issue-drop-zone__file.ignore-inline-attach")).sendKeys(path);
        driver.findElement(By.cssSelector(""));
    }

    @Test(description = "Valid Login",priority = 1)
    public void login() throws InterruptedException {
        authorization(username, password);
        Thread.sleep(30000);
        String profileName = driver.findElement(By.id("header-details-user-fullname")).getAttribute("data-username");
        Assert.assertEquals(profileName, username);
    }

    @Test (description = "Invalid Login")
    public void invalidLogin() throws InterruptedException {
        authorization(username, password+1 );
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".aui-message.error")));
        String errorMessage = driver.findElement(By.cssSelector(".aui-message.error")).getText();
        Assert.assertEquals(errorMessage,"Sorry, your username and password are incorrect - please try again.");
    }

    @Test(description = "Create issue with attachment", dependsOnMethods = {"login"})
    public void createTicket() throws InterruptedException {
        createTask();
        File file = new File("C:\\Users\\Oleh\\Desktop\\65fa961e-8f22-4fe6-a420-3c3c26dd2953.jpg._CB289161999__SL300__.jpg");
        int original = file.hashCode();
        System.out.println(original);
        uploadAttachment(driver, file.getPath());
        //   driver.findElement(By.cssSelector("#create-issue-submit")).click();
        Thread.sleep(2000);
    }

    @Test(description = "Delete tasks", dependsOnMethods = {"login"})
    public void removeTasksWhichWasCreated() throws InterruptedException {
        authorization(username, password);
        driver.findElement(By.cssSelector("#find_link")).click();
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("#filter_lnk_reported_lnk")).click();
        while (!driver.findElements(By.cssSelector(".issue-list .issue-link-key")).isEmpty()) {
            driver.findElement(By.cssSelector(".issue-list .issue-link-key")).click();
            new FluentWait<WebDriver>(driver).withTimeout(5, TimeUnit.SECONDS).pollingEvery(500, TimeUnit.MILLISECONDS)
                    .ignoring(InvalidElementStateException.class).until(new Function<WebDriver, WebElement>() {
                public WebElement apply(WebDriver browser) {
                    return driver.findElement(By.cssSelector("#opsbar-operations_more"));
                }
            });
            // TODO: 25-Feb-18  investigate how to remove sleep
            Thread.sleep(2000);

            String currentIssue = driver.findElement(By.cssSelector("#key-val")).getAttribute("data-issue-key");
            driver.findElement(By.cssSelector("#opsbar-operations_more")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#opsbar-operations_more_drop")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", driver.findElement(By.cssSelector("#opsbar-operations_more_drop #delete-issue")));
            driver.findElement(By.cssSelector("#opsbar-operations_more_drop #delete-issue")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#delete-issue-submit")));
            driver.findElement(By.cssSelector("#delete-issue-submit")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".aui-message.aui-message-success.success.closeable.shadowed.aui-will-close")));
            Assert.assertEquals(driver.findElement(By.cssSelector(".aui-message.aui-message-success.success.closeable.shadowed.aui-will-close")).getText(), currentIssue + " has been deleted.");
            // TODO: 25-Feb-18  Investigate how to remove it
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".aui-message.aui-message-success.success.closeable.shadowed.aui-will-close")));
        }
    }


    @Test(description = "Check User Dashboard", dependsOnMethods = {"login"})
    public void checkUsersDashboard() throws InterruptedException {
        authorization(username, password);
        driver.findElement(By.id("system-admin-menu")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("system-admin-menu-content")));
        driver.findElement(By.id("admin_users_menu")).click();
        String adminAccessHeader = driver.findElement(By.cssSelector(".aui-page-panel-content header h1")).getText();
        //todo write if else, in case if pass doesn't need
        if (!StringUtils.isEmpty(adminAccessHeader)) {
            driver.findElement(By.id("login-form-authenticatePassword")).sendKeys(password);
            driver.findElement(By.id("login-form-submit")).click();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#admin-nav-heading .aui-nav-selected")));
        String currentTab = driver.findElement(By.cssSelector("#admin-nav-heading .aui-nav-selected")).getText();
        Assert.assertEquals(currentTab, "User management");
        int startCountUsersOnThePage = Integer.parseInt(driver.findElement(By.cssSelector(".results-count-start")).getText());
        int endCountUsersOnThePage = Integer.parseInt(driver.findElement(By.cssSelector(".results-count-end")).getText());
        System.out.println(endCountUsersOnThePage);
        int usersOnThePage = endCountUsersOnThePage - startCountUsersOnThePage + 1;
        int usersInTheTable = driver.findElements(By.cssSelector(".vcard.user-row")).size();
        System.out.println(usersOnThePage);
        Assert.assertEquals(usersInTheTable, usersOnThePage);
    }


    // TODO: 25-Feb-18  NOT FINISHED
    @Test
    public void downloadFile() throws InterruptedException {
        authorization(username, password);
        driver.get("http://jira.hillel.it:8080/browse/GQR-375");
        Thread.sleep(3000);
        String attr =
                driver.findElements(By.cssSelector(".attachment-content.js-file-attachment"))
                        .get(1).getAttribute("data-downloadurl");
        String[] someNew = attr.split("http.*");
        System.out.println(someNew[0]);
        driver.get("http://jira.hillel.it:8080/secure/attachment/12263/picture.jpg");
        Thread.sleep(3000);
        WebElement Image = driver.findElement(By.cssSelector("[data-gr-c-s-loaded='true'"));
        Actions action = new Actions(driver);
        action.contextClick(Image).build().perform();
        Thread.sleep(3000);
    }


}
