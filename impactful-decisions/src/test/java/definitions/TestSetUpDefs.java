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

}
