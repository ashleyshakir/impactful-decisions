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