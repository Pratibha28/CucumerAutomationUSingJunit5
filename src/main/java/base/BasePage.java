package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import utility.ActionMethods;

/**
 * Page objects should accept WebDriver via constructor for clarity and parallel-safety.
 */
public abstract class BasePage {
    protected final WebDriver driver;
    protected final ActionMethods action;

    public BasePage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalStateException("Driver is null when creating page. Ensure Hooks created driver first.");
        }
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.action = new ActionMethods(driver);
    }
}
