Feature: Rest API functionalities

  Scenario: User able to create, view, update, and delete decisions
    Given I am logged into my account securely
    When I create a new decision and add a new title
    Then My new decision is created
    When I create a new decision with an existing title
    Then Then a DecisionExistsException is thrown
    When I want to view a decision
    Then The decision is retrieved
    When I want to view my list of decisions
    Then The list of decisions is retrieved
    When I update a decision
    Then The decision is updated
    When I delete a decision
    Then The decision is deleted

  Scenario: User able to add, update, and delete decision criteria
    Given a decision is available
    When I click to add criteria
    Then The criteria is added to the decision
    When I update criteria name or weight
    Then The criteria is updated
    When I delete criteria from a decision
    Then It is deleted from the decision

  Scenario: User able to add, update, and delete decision options
    Given I am editing a decision
    When I click to add an option
    Then The option is added to the decision
    When I update an option name
    Then The option name is updated
    When I delete an option from a decision
    Then The option is deleted from the decision

  Scenario: User able to add, update, and delete option pros and cons
    Given I am editing an option
    When I click to add a new pro or con
    Then The new pro or con is added
    When I update a pro or con
    Then The pro or con is updated
    When I delete a pro or con
    Then The pro or con is deleted

  Scenario: Option recommendation provides accurate results
    Given I have a decision to analyze
    When I want to see the score of an option
    Then That options score is shown
#    When I click to analyze the decision
#    Then I am given a recommended option