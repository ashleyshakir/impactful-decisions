package definitions;

import com.example.impactfuldecisions.ImpactfulDecisionsApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.logging.Logger;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ImpactfulDecisionsApplication.class)
public class TestSetUpDefs {

    public static final Logger logger = Logger.getLogger(UserAuthenticationTestDefs.class.getName());

    // Random port
    @LocalServerPort
    public String port;

    // Base URL for testing
    public static final String BASE_URL = "http://localhost:";

    // Public Endpoints
    public static final String helloEndpoint = "/auth/users/hello/";
    public static final String registerEndpoint = "/auth/users/register/";
    public static final String loginEndpoint= "/auth/users/login/";

    // Decision Endpoints
    public static final String decisionsEndpoint = "/api/decisions/";
    public static final String singleDecisionEndpoint = "/api/decisions/1/";

    // Criteria Endpoints
    public static final String criteriaEndpoint = "/api/decisions/1/criteria/";
    public static final String singleCriteriaEndpoint = "/api/decisions/1/criteria/1/";

    // Option Endpoints
    public static final String optionsEndpoint = "/api/decisions/1/options/";


}
