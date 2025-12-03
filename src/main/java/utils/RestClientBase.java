package utils;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static io.restassured.RestAssured.given;

public class RestClientBase {

    protected final RequestSpecification baseSpec;

    protected String lastRequestMethod;
    protected Map<String, ?> lastRequestHeaders;
    protected Object lastRequestBody;
    protected String lastRequestUrl;
    protected Response lastResponse;

    public String getLastRequestMethod() {
        return lastRequestMethod;
    }

    public Map<String, ?> getLastRequestHeaders() {
        return lastRequestHeaders;
    }

    public Object getLastRequestBody() {
        return lastRequestBody;
    }

    public String getLastRequestUrl() {
        return lastRequestUrl;
    }

    public Response getLastResponse() {
        return lastResponse;
    }


    public RestClientBase(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("Base URL cannot be empty");
        }

        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new ErrorLoggingFilter())
                .build();
    }

    private Response execute(String method, String endpoint, Map<String, ?> headers, Object body, Function<RequestSpecification, Response> action) {
        RequestSpecification spec = RestAssured.given().spec(baseSpec);

        if (headers != null && !headers.isEmpty()) {
            spec.headers(headers);
        }
        if (body != null) {
            spec.body(body);
        }

        this.lastRequestMethod = method;
        this.lastRequestUrl = endpoint;
        this.lastRequestHeaders = headers != null ? headers : new HashMap<>();
        this.lastRequestBody = body;

        Response response = action.apply(spec);

        this.lastResponse = response;

        return response;
    }



    @Step("GET {endpoint}")
    protected Response get(String endpoint) {
        return execute("GET", endpoint, null, null,
                spec -> spec.get(endpoint));
    }

    @Step("GET {endpoint} with query params: {queryParams}")
    protected Response get(String endpoint, Map<String, ?> queryParams) {
        return execute("GET", endpoint, null, null,
                spec -> spec.queryParams(queryParams).get(endpoint));
    }

    @Step("POST {endpoint} with body")
    protected Response post(String endpoint, Object body) {
        return execute("POST", endpoint, null, body,
                spec -> spec
                .post(endpoint));
    }

    @Step("PUT {endpoint} with body")
    protected Response put(String endpoint, Object body) {
        return execute("PUT", endpoint, null, body,
                spec -> spec
                .put(endpoint));
    }

    @Step("PUT {endpoint} with body and headers")
    protected Response put(String endpoint, Object body, Map<String, ?> headers) {
        return execute("PUT", endpoint, headers, body,
                spec -> spec
                .put(endpoint));
    }

    @Step("DELETE {endpoint}")
    protected Response delete(String endpoint) {
        return execute("DELETE", endpoint, null, null,
                spec -> spec
                .delete(endpoint));
    }

    @Step("Extract response as {clazz}")
    protected <T> T extract(Response response, Class<T> clazz) {
        return response.as(clazz);
    }

    protected void assertStatus(Response response, int expected) {
        Assert.assertEquals(response.statusCode(), expected,
                "Unexpected status code! Response body:\n" + response.getBody().asPrettyString());
    }

    protected void logResponsePretty(Response response) {
        System.out.println("=== RESPONSE BODY ===");
        System.out.println(response.getBody().asPrettyString());
    }
}
