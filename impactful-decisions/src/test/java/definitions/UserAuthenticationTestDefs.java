package definitions;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class UserAuthenticationTestDefs extends TestSetUpDefs{
    private static Response response;
    private static ResponseEntity<String> responseEntity;
    private static JSONObject requestBody;

    // ------ Public Endpoint Tests for saying hello ------
    @Given("A valid public endpoint")
    public void aValidPublicEndpoint() {
        responseEntity = new RestTemplate().exchange(BASE_URL + port + helloEndpoint, HttpMethod.GET,null, String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @When("I say hello")
    public void iSayHello() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        response = request.get(BASE_URL + port + helloEndpoint);
    }

    @Then("Hello is shown")
    public void helloIsShown() {
        JsonPath jsonPath = response.jsonPath();
        String message = jsonPath.get("message");
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertEquals("Hello", message);
    }
    // ------ Public Endpoint Tests for registering a new user ------
    @Given("I am on the registration page")
    public void iAmOnTheRegistrationPage() {
        RequestSpecification request = RestAssured.given();
        response = request.get(BASE_URL + port + registerEndpoint);
        JsonPath jsonPath = response.jsonPath();
        String actualPath = jsonPath.getString("path");
        Assert.assertEquals(registerEndpoint,actualPath);
    }

    @When("I fill in the registration form with valid information")
    public void iFillInTheRegistrationFormWithValidInformation() throws JSONException {
        logger.info("The user fills in the registration form with valid information");
        RestAssured.baseURI = BASE_URL;
        RequestSpecification request = RestAssured.given();
        requestBody = new JSONObject();
        requestBody.put("userName","decisionmaker101");
        requestBody.put("emailAddress","impactfuldecisionsteam@gmail.com");
        requestBody.put("password","password12345");
        request.header("Content-Type","application/json");
        response = request.body(requestBody.toString()).post(BASE_URL + port + registerEndpoint);
    }

    @Then("my account is created, and I am logged in")
    public void myAccountIsCreatedAndIAmLoggedIn() {
        logger.info("Calling the user is registered");
        Assert.assertEquals(201,response.getStatusCode());
    }

}
