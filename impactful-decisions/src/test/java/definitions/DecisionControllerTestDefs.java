package definitions;

import com.example.impactfuldecisions.models.analysis.RecommendedOption;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.service.DecisionAnalysisService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class DecisionControllerTestDefs extends TestSetUpDefs{

    private static ResponseEntity<String> responseEntity;
    private static double optionScore1;
    private static double optionScore2;
    private static RecommendedOption recommendation;

    @Autowired
    private DecisionRepository decisionRepository;
    @Autowired
    private DecisionAnalysisService decisionAnalysisService;

    // For testing secure endpoints:
    public String getJWTToken() throws JSONException {
        logger.info("TestJWTToken: Generated");
        // Set the base URI and create a request
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();

        // Set the content-type header to indicate JSON data
        request.header("Content-Type", "application/json");

        // Create a JSON request body with user email and password
        JSONObject requestBody = new JSONObject();
        requestBody.put("emailAddress", "impactfuldecisionsteam@gmail.com");
        requestBody.put("password", "password12345");

        // Send a POST request to the authentication endpoint
        Response response = request.body(requestBody.toString()).post(BASE_URL + port + loginEndpoint);

        // Extract and return the JWT token from the authentication response
        return response.jsonPath().getString("jwt");
    }
    private HttpHeaders createAuthHeaders() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getJWTToken());
        headers.add("Content-Type", "application/json");
        return headers;
    }

    @Given("I am logged into my account securely")
    public void iAmLoggedIntoMyAccountSecurely() throws JSONException {
        logger.info("Calling I am a logged in user");
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + getJWTToken());
    }

    @When("I create a new decision and add a new title")
    public void iCreateANewDecisionAndAddANewTitle() throws JSONException {
        logger.info("Calling I create a new decision");
        // Creating authorization and content type
        HttpHeaders headers = createAuthHeaders();
        // Creating a new decision object to pass through
        JSONObject requestBody = new JSONObject();
        requestBody.put("title", "New Test Decision");
        requestBody.put("description", "New Decision Description");
        // Build our post request
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        responseEntity = new RestTemplate().exchange(BASE_URL + port + decisionsEndpoint, HttpMethod.POST, entity, String.class);
    }

    @Then("My new decision is created")
    public void myNewDecisionIsCreated() {
        logger.info("Calling the new decision is created");
        Assert.assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
    }

    @When("I create a new decision with an existing title")
    public void iCreateANewDecisionWithAnExistingTitle() throws JSONException {
        logger.info("Calling I create a new decision with an existing title");
        // Creating authorization and content type
        HttpHeaders headers = createAuthHeaders();
        // Creating a new decision object to pass through
        JSONObject requestBody = new JSONObject();
        requestBody.put("title", "New Test Decision");
        // Build our post request
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        responseEntity = new RestTemplate().exchange(BASE_URL + port + decisionsEndpoint, HttpMethod.POST, entity, String.class);
    }

    @Then("Then a DecisionExistsException is thrown")
    public void thenADecisionExistsExceptionIsThrown() {
        logger.info("Calling Then a DecisionExistsException is thrown");
        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @When("I want to view a decision")
    public void iWantToViewADecision() throws JSONException {
        logger.info("Calling I click to view a decision");
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleDecisionEndpoint, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class);
    }

    @Then("The decision is retrieved")
    public void theDecisionIsRetrieved() {
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @When("I want to view my list of decisions")
    public void iWantToViewMyListOfDecisions() throws JSONException {
        logger.info("Calling I click to view all decisions");
        responseEntity = new RestTemplate().exchange(BASE_URL + port + decisionsEndpoint, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class);
    }

    @Then("The list of decisions is retrieved")
    public void theListOfDecisionsIsRetrieved() {
        logger.info("Calling I can see all my decisions");
        String responseBody = String.valueOf(responseEntity.getBody());
        logger.info("Response body: " + responseBody);
        if (responseBody != null && !responseBody.isEmpty()) {
            // Parse the JSON response to extract the list of decisions
            List<Map<String, String>> decisions = JsonPath.from(String.valueOf(responseEntity.getBody())).get("data");
            // Assert that the response status is OK and there are decisions available
            Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
            Assert.assertTrue(decisions.size() > 0);
        } else {
            // If the response is null or empty, fail the test
            Assert.fail("Received null or empty response");
        }
    }

    @When("I update a decision")
    public void iUpdateADecision() throws JSONException {
        logger.info("Calling I click to update a decision");
        JSONObject requestBody = new JSONObject();
        requestBody.put("title","This is my updated decision title");
        requestBody.put("description","This is my updated description");
        requestBody.put("setResolved","true");
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleDecisionEndpoint, HttpMethod.PUT, entity, String.class);
    }

    @Then("The decision is updated")
    public void theDecisionIsUpdated() {
        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @When("I delete a decision")
    public void iDeleteADecision() throws JSONException {
        logger.info("Calling when I delete a decision");
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleDecisionEndpoint, HttpMethod.DELETE, new HttpEntity<>(createAuthHeaders()), String.class);
    }

    @Then("The decision is deleted")
    public void theDecisionIsDeleted() {
        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Given("a decision is available")
    public void aDecisionIsAvailable() throws JSONException {
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleDecisionEndpoint, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @When("I click to add criteria")
    public void iClickToAddCriteria() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name","Price");
        requestBody.put("weight",4D);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL + port + criteriaEndpoint, HttpMethod.POST, entity, String.class);
    }

    @Then("The criteria is added to the decision")
    public void theCriteriaIsAddedToTheDecision() {
        logger.info("Calling the criteria is added to the decision");
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @When("I update criteria name or weight")
    public void iUpdateCriteriaNameOrWeight() throws JSONException {
        logger.info("Calling I update criteria name or weight");
        JSONObject requestBody = new JSONObject();
        requestBody.put("name","updated name");
        requestBody.put("weight",2L);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleCriteriaEndpoint, HttpMethod.PUT, entity, String.class);

    }

    @Then("The criteria is updated")
    public void theCriteriaIsUpdated() {
        logger.info("Calling the criteria is updated");
        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @When("I delete criteria from a decision")
    public void iDeleteCriteriaFromADecision() throws JSONException {
        logger.info("I delete criteria from a decision");
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleCriteriaEndpoint, HttpMethod.DELETE, new HttpEntity<>(createAuthHeaders()), String.class);
    }

    @Then("It is deleted from the decision")
    public void itIsDeletedFromTheDecision() {
        logger.info("The criteria is deleted from the decision");
        Assert.assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
    }

    @Given("I am editing a decision")
    public void iAmEditingADecision() throws JSONException {
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleDecisionEndpoint, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @When("I click to add an option")
    public void iClickToAddAnOption() throws JSONException {
        logger.info("Calling I click to add an option");
        JSONObject requestBody = new JSONObject();
        requestBody.put("name","Go to London");
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL + port + optionsEndpoint, HttpMethod.POST, entity, String.class);
    }

    @Then("The option is added to the decision")
    public void theOptionIsAddedToTheDecision() {
        logger.info("Calling the option is added to the decision");
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @When("I update an option name")
    public void iUpdateAnOptionName() throws JSONException {
        logger.info("Calling I update an option name");
        JSONObject requestBody = new JSONObject();
        requestBody.put("name","updated name");
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleOptionEndpoint, HttpMethod.PUT, entity, String.class);
    }

    @Then("The option name is updated")
    public void theOptionNameIsUpdated() {
        logger.info("Calling the option is updated");
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @When("I delete an option from a decision")
    public void iDeleteAnOptionFromADecision() throws JSONException {
        logger.info("Calling I delete an option from a decision");
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleOptionEndpoint, HttpMethod.DELETE, new HttpEntity<>(createAuthHeaders()), String.class);
    }

    @Then("The option is deleted from the decision")
    public void theOptionIsDeletedFromTheDecision() {
        logger.info("Calling the option is deleted from the decision.");
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Given("I am editing an option")
    public void iAmEditingAnOption() throws JSONException {
        logger.info("Calling I am editing an option");
        JSONObject requestBody = new JSONObject();
        requestBody.put("name","Go to London");
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL + port + optionsEndpoint, HttpMethod.POST, entity, String.class);
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @When("I click to add a new pro or con")
    public void iClickToAddANewProOrCon() throws JSONException {
        logger.info("Calling I am adding a pro or con");
        JSONObject requestBody = new JSONObject();
        requestBody.put("type","pro");
        requestBody.put("description","Explore a new country");
        requestBody.put("rating",3.5);
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(),createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL +  port + proConsEndpoint, HttpMethod.POST, entity, String.class);
    }

    @Then("The new pro or con is added")
    public void theNewProOrConIsAdded() {
        logger.info("Calling the new pro or con is added");
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @When("I update a pro or con")
    public void iUpdateAProOrCon() throws JSONException {
        logger.info("Calling I update a pro or con");
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating",4.0);
        requestBody.put("description","updated description");
        requestBody.put("type","pro");
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), createAuthHeaders());
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleProConEndpoint, HttpMethod.PUT, entity, String.class);
    }

    @Then("The pro or con is updated")
    public void theProOrConIsUpdated() {
        logger.info("Calling the pro or con is updated");
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @When("I delete a pro or con")
    public void iDeleteAProOrCon() throws JSONException {
        logger.info("Calling I delete a pro or con");
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleProConEndpoint, HttpMethod.DELETE, new HttpEntity<>(createAuthHeaders()), String.class);
    }

    @Then("The pro or con is deleted")
    public void theProOrConIsDeleted() {
        logger.info("Calling the pro or con is deleted");
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Given("I have a decision to analyze")
    public void iHaveADecisionToAnalyze() {
        logger.info("Calling a decision is available");
        Assert.assertNotNull(decisionRepository.findById(1L));
    }

    @When("I want to see the score of an option")
    public void iWantToSeeTheScoreOfAnOption() {
        logger.info("Calling I click to analyze the decision");
        optionScore1 = decisionAnalysisService.calculateOptionScore(1L,1L);
        optionScore2 = decisionAnalysisService.calculateOptionScore(1L,2L);
    }

    @Then("That options score is shown")
    public void thatOptionsScoreIsShown() {
        Assert.assertEquals(-4.0,optionScore1,0.0001);
        Assert.assertEquals(4.4,optionScore2,0.0001);
    }
    @When("I click to analyze the decision")
    public void iClickToAnalyzeTheDecision() {
        logger.info("Calling I click to analyze the decision");
        recommendation = decisionAnalysisService.calculateAllOptionScores(1L);
    }

    @Then("I am given a recommended option")
    public void iAmGivenARecommendedOption() {
        Assert.assertEquals("Stay home",recommendation.getRecommendedOption().getName());
        Assert.assertEquals(4.4,recommendation.getOptionScores().get(2L),0.00001);
    }

    @Given("I have a new decision to analyze")
    public void iHaveANewDecisionToAnalyze() throws JSONException {
        responseEntity = new RestTemplate().exchange(BASE_URL + port + singleDecisionEndpoint, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


    @When("I want to analyze the decision")
    public void iWantToAnalyzeTheDecision() throws JSONException {
        responseEntity = new RestTemplate().exchange(BASE_URL + port + recommendationEndpoint, HttpMethod.GET, new HttpEntity<>(createAuthHeaders()), String.class);
    }

    @Then("I am shown a recommended option")
    public void iAmShownARecommendedOption() {
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


}
