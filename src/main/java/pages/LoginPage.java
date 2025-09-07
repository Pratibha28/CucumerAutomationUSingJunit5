package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import base.BasePage;

public class LoginPage extends BasePage {

	 public LoginPage(org.openqa.selenium.WebDriver driver) {
	        super(driver);
	    }
	@FindBy(xpath = "//input[@name='user-name']")
	WebElement username;

	@FindBy(xpath = "//input[@name='password']")
	WebElement password;

	@FindBy(xpath = "//input[@value='LOGIN']")
	WebElement loginButton;

	@FindBy(xpath = "//h3[@data-test='error']")
	WebElement errorMsg;

	public void loginWithValidCredential(String usernames, String passwords) {
		username = action.genericFluientwait(10, ExpectedConditions.visibilityOf(username));
		password = action.genericFluientwait(10, ExpectedConditions.visibilityOf(password));
		loginButton = action.genericFluientwait(10, ExpectedConditions.elementToBeClickable(loginButton));
		action.type(username, usernames);
		action.type(password, passwords);
		action.click(loginButton, "Click on login");
		// sreturn new ProductPage();
	}

	public String validateDashboard() {
		String pageUrl = driver.getCurrentUrl();
		return pageUrl;
	}

	public String getLoginErrorMessage() {

		action.genericFluientwait(10, ExpectedConditions.visibilityOf(errorMsg));

		// action.visibilityOfWebElementLocated(errorMsg);
		String message = errorMsg.getText();
		return message;

	}

	public String loginWithInvalidCredential(String username1, String password1) {

		action.type(username, username1);
		action.type(password, password1);
		action.click(loginButton, "Click on login");
		action.genericFluientwait(10, ExpectedConditions.visibilityOf(errorMsg));

		// action.visibilityOfWebElementLocated(errorMsg);
		String message = errorMsg.getText();
		return message;
	}
}
