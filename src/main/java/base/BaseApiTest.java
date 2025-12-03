package base;

import org.testng.annotations.Listeners;
import utils.RestClientBase;
import org.testng.annotations.BeforeClass;

@Listeners({base.TestListener.class})
public class BaseApiTest extends RestClientBase{

    public BaseApiTest() {
        super(System.getenv("API_BASE_URL"));
    }

    @BeforeClass
    public void validateEnv() {
        if (System.getenv("API_BASE_URL") == null) {
            throw new RuntimeException("API_BASE_URL environment variable missing");
        }
    }

    public RestClientBase getClient() {
        return this;
    }
}
