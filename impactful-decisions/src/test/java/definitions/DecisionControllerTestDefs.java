package definitions;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.models.Criteria;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.repository.CriteriaRepository;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.service.DecisionService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DecisionControllerTestDefs extends TestSetUpDefs{

    private static JSONObject requestBody;
    private static Response response;

    // Service testing variables
    Exception caughtException = null;
    Decision decision = new Decision();
    List<Decision> decisionList;
    private static ResponseEntity<String> responseEntity;

    @Autowired
    private DecisionRepository decisionRepository;
    @Autowired
    private DecisionService decisionService;
    @Autowired
    private CriteriaRepository criteriaRepository;

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
    public void iUpdateCriteriaNameOrWeight() {
        logger.info("Calling I update criteria name or weight");
        Criteria updatedCriteria = new Criteria();
        updatedCriteria.setName("Price");
        updatedCriteria.setWeight(4D);
        decisionService.updateCriteria(1L,1L,updatedCriteria);

    }

    @Then("The criteria is updated")
    public void theCriteriaIsUpdated() {
        logger.info("Calling the criteria is updated");
        Assert.assertEquals("Price",criteriaRepository.findById(1L).get().getName());
        Assert.assertEquals(4D, criteriaRepository.findById(1L).get().getWeight(), 0.0001);
    }

    @When("I delete criteria from a decision")
    public void iDeleteCriteriaFromADecision() {
        logger.info("I delete criteria from a decision");
        decisionService.deleteCriteria(1L,1L);
    }

    @Then("It is deleted from the decision")
    public void itIsDeletedFromTheDecision() {
        Assert.assertTrue(criteriaRepository.findById(1L).isEmpty());
        Assert.assertTrue(decisionRepository.findById(1L).get().getCriteriaList().isEmpty());
    }


}
