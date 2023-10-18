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

import java.time.Clock;
import java.time.LocalDate;

public class DecisionControllerTestDefs extends TestSetUpDefs{

    private static JSONObject requestBody;
    private static Response response;
    Exception caughtException = null;

    @Autowired
    private DecisionRepository decisionRepository;
    @Autowired
    private DecisionService decisionService;


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


}
