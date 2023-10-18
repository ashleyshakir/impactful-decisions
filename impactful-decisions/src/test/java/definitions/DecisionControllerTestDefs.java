package definitions;

import com.example.impactfuldecisions.exceptions.DecisionExistsException;
import com.example.impactfuldecisions.models.Decision;
import com.example.impactfuldecisions.repository.DecisionRepository;
import com.example.impactfuldecisions.service.DecisionService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class DecisionControllerTestDefs extends TestSetUpDefs{

    private static JSONObject requestBody;
    private static Response response;

    // Service testing variables
    Exception caughtException = null;
    Decision decision = new Decision();
    List<Decision> decisionList;

    @Autowired
    private DecisionRepository decisionRepository;
    @Autowired
    private DecisionService decisionService;

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
        RequestSpecification request = RestAssured.given();
        requestBody = new JSONObject();
        requestBody.put("emailAddress","impactfuldecisionsteam@gmail.com");
        requestBody.put("password","password12345");
        response = request.contentType(ContentType.JSON).body(requestBody.toString()).post(BASE_URL + port + loginEndpoint);
        Assert.assertEquals(200, response.getStatusCode());
    }

    @When("I create a new decision and add a new title")
    public void iCreateANewDecisionAndAddANewTitle() {
        logger.info("Calling I create a new decision and add a new title");
        Decision decision = new Decision();
        decision.setTitle("Should I buy a new car?");
        decision.setDescription("Deciding between a Toyota 4Runner and a Ford Bronco");
        decision.setCreationDate(LocalDate.now(Clock.systemDefaultZone()));
        decisionService.createDecision(decision);
    }

    @Then("My new decision is created")
    public void myNewDecisionIsCreated() {
        logger.info("Calling then my new decision is created");
        Assert.assertNotNull(decisionRepository.findByTitle("Should I buy a new car?"));
    }

    @When("I create a new decision with an existing title")
    public void iCreateANewDecisionWithAnExistingTitle() {
        logger.info("Calling I create a new decision with an existing title");
        try{
            Decision decision = new Decision();
            decision.setTitle("Should I buy a new car?");
            decisionService.createDecision(decision);
        } catch (Exception e){
            caughtException = e;
        }
    }

    @Then("Then a DecisionExistsException is thrown")
    public void thenADecisionExistsExceptionIsThrown() {
        logger.info("Calling Then a DecisionExistsException is thrown");
        Assert.assertNotNull("Expected an exception to be thrown", caughtException);
        Assert.assertTrue("Expected DecisionExistsException but found " + caughtException,
                caughtException instanceof DecisionExistsException);
    }

    @When("I want to view a decision")
    public void iWantToViewADecision() {
        logger.info("Calling I want to view a decision");
        decision = decisionService.getDecision(1L);
    }

    @Then("The decision is retrieved")
    public void theDecisionIsRetrieved() {
        Assert.assertNotNull(decision);
    }

    @When("I want to view my list of decisions")
    public void iWantToViewMyListOfDecisions() {
        logger.info("Calling I want to view my list of decisions");
        decisionList = decisionService.getUserDecisions();
    }

    @Then("The list of decisions is retrieved")
    public void theListOfDecisionsIsRetrieved() {
        Assert.assertNotNull(decisionList);
    }
    @When("I update a decision")
    public void iUpdateADecision() {
        logger.info("Calling I update a decision");
        Decision updatedDecision = new Decision();
        updatedDecision.setTitle("This is my updated decision title");
        updatedDecision.setResolved(true);
        decisionService.updateDecision(1L, updatedDecision);
    }

    @Then("The decision is updated")
    public void theDecisionIsUpdated() {
        Assert.assertEquals("This is my updated decision title",decisionRepository.findById(1L).get().getTitle());
        Assert.assertTrue(decisionRepository.findById(1L).get().isResolved());
    }
    @When("I delete a decision")
    public void iDeleteADecision() {
        logger.info("Calling when I delete a decision");
        decisionService.deleteDecision(1L);
    }

    @Then("The decision is deleted")
    public void theDecisionIsDeleted() {
        Assert.assertTrue(decisionRepository.findById(1L).isEmpty());
    }


}
