Feature: Authentication/Authorization of User

  Scenario: User able to access public endpoints
    Given A valid public endpoint
    When I say hello
    Then Hello is shown

  Scenario: User able to register an account
    Given I am on the registration page
    When I fill in the registration form with valid information
    Then my account is created, and I am logged in

  Scenario: User able to log in to their account
    Given I am on the login page
    When I enter my credentials and click the login button
    Then I am logged into my account
    And I receive a JWT token