package utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BaseTest;

public class ActionMethods {

    private Duration defaultWait = Duration.ofSeconds(10);

    private final WebDriver driver;

    public ActionMethods(WebDriver driver) {
        this.driver = driver;
    }

    public boolean waitForElementToDisappear(By element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(element));
        return true;
    }

    public boolean type(WebElement ele, String text) {
        boolean flag = false;
        try {
            flag = ele.isDisplayed();
            ele.clear();
            ele.sendKeys(text);
            flag = true;
        } catch (Exception e) {
            System.out.println("Location Not found: " + e.getMessage());
            flag = false;
        } finally {
            if (!flag) {
                System.out.println("Unable to enter value");
            }
        }
        return flag;
    }

    public boolean click(WebElement locator, String locatorName) {
        boolean flag = false;
        try {
            locator.click();
            flag = true;
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            if (!flag) {
                System.out.println("Click Unable to click on \"" + locatorName + "\"");
            }
        }
    }

    public String getCurrentURL(WebDriver driver) {
        return driver.getCurrentUrl();
    }

    public boolean visibilityOfElement(WebDriver driver, WebElement element, int timeOut) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeOut));
        wait.until(ExpectedConditions.visibilityOf(element));
        return true;
    }

    public boolean selectByIndex(WebElement element, int index) {
        boolean flag = false;
        try {
            Select s = new Select(element);
            s.selectByIndex(index);
            flag = true;
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (flag) {
                System.out.println("Option selected by Index");
            } else {
                System.out.println("Option not selected by Index");
            }
        }
    }

    public boolean findElement(WebDriver driver, WebElement ele) {
        boolean flag = false;
        try {
            ele.isDisplayed();
            flag = true;
        } catch (Exception e) {
            flag = false;
        } finally {
            if (flag) {
                System.out.println("Successfully Found element");
            } else {
                System.out.println("Unable to locate element");
            }
        }
        return flag;
    }

    public boolean isDisplayed(WebDriver driver, WebElement ele) {
        boolean flag = findElement(driver, ele);
        try {
            if (flag) {
                flag = ele.isDisplayed();
                if (flag) {
                    System.out.println("The element is Displayed");
                } else {
                    System.out.println("The element is not Displayed");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Not displayed: " + e.getMessage());
        }
        return flag;
    }

    public String screenShot(WebDriver driver, String filename) {
        String dateName = new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date());
        TakesScreenshot takeScreenShot = (TakesScreenshot) driver;
        File source = takeScreenShot.getScreenshotAs(OutputType.FILE);

        String destination = System.getProperty("user.dir") + File.separator + "Screenshots" + File.separator
                + filename + "_" + dateName + ".png";

        try {
            FileUtils.copyFile(source, new File(destination));
        } catch (Exception e) {
            System.out.println("screenshot failed: " + e.getMessage());
        }

        return destination;
    }

    public boolean validateExpectedURL(String url) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.urlToBe(url));
    }

    public Boolean visibilityOfElementLocated(By element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(element)).isDisplayed();
    }

    public Boolean visibilityOfWebElementLocated(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOf(element)).isDisplayed();
    }

    public List<WebElement> ListOfWebelement(By element) {
        return driver.findElements(element); // fixed: return actual list
    }

    public void acceptAlertMethod() {
        if (driver == null) {
            System.out.println("Driver is null!");
            return;
        }
        driver.switchTo().alert().accept();
    }

    public void fluientWait(int time, WebElement element) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(time))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class);
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public <T> T genericFluientwait(int time, ExpectedCondition<T> condition) {
        try {
            Wait<WebDriver> wait = new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(time))
                    .pollingEvery(Duration.ofSeconds(1))
                    .ignoring(NoSuchElementException.class)
                    .ignoring(StaleElementReferenceException.class)
                    .ignoring(ElementClickInterceptedException.class);

            return wait.until(condition);
        } catch (TimeoutException e) {
            System.err.println("❌ Timeout after " + time + " seconds for condition: " + condition);
            return null;
        }
    }
}
