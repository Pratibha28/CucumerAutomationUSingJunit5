Feature: Login functionality

  
    
    
    @login
Scenario Outline: Test login with multiple credentials
    Given I launch the browser and navigate to the login page
    When I log in using "<Username>" and "<Password>"
    Then I validate the login result using "<url>"
    
Examples:
|Username|Password|url|
|standard_user|secret_sauce|https://www.saucedemo.com/v1/inventory.html|
|standard_user|secret_sauce|https://www.saucedemo.com/v1/inventory1.html|


Scenario Outline: Test login with multiple credentials
    Given I launch the browser and navigate to the login page
    When I log in using "<Username>" and "<Password>"
    Then I validate the login result using "<url>"
    
Examples:
|Username|Password|url|
|standard_user|secret_sauce|https://www.saucedemo.com/v1/inventory.html|
|standard_user|secret_sauce|https://www.saucedemo.com/v1/inventory1.html|



