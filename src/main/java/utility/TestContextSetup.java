package utility;

import org.openqa.selenium.WebDriver;

import base.BaseTest;
import pages.LoginPage;

public class TestContextSetup {
    private LoginPage loginPage;

    public TestContextSetup() {
        // pico container will construct this; do NOT create pages here
    }

    public LoginPage loginPage() {
        // ensure driver is created for current thread before creating page object
        BaseTest.createDriverIfNeeded();
        if (loginPage == null) {
            loginPage = new LoginPage(getDriver());
        }
        return loginPage;
    }

    public WebDriver getDriver() {
        return BaseTest.getDriver();
    }
}
