package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import pages.LoginPage;
import utility.TestContextSetup;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;

public class LoginStepDefination {
	LoginPage loginPage;
	TestContextSetup testContext;
	
	public LoginStepDefination(TestContextSetup testContext) {
		this.testContext = testContext;

	}
	@Given("I launch the browser and navigate to the login page")
	public void i_launch_the_browser_and_navigate_to_the_login_page() {
		testContext.loginPage();
	    System.out.println("🌐 Browser launched and navigated to login page");  
	}

	@When("I log in using {string} and {string}")
	public void i_log_in_using_and(String username, String password) {
		testContext.loginPage().loginWithValidCredential(username, password);
	}

	@Then("I validate the login result using {string}")
	public void i_validate_the_login_result(String url) {

         String actualUrl =testContext.loginPage().validateDashboard();
         assertEquals(url, actualUrl);
         
	}

}
