package definitions;

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


}
