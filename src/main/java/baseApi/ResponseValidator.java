package baseApi;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Map;

public class ResponseValidator {

    public static final String EMPTY_STRING = "";

    private final Response response;

    public ResponseValidator(Response response) {
        this.response = response;
        printResponseBody();
    }

    public ResponseValidator verifyResponseValue(String param, Object expectedValue) {
        String valueFromParam = getResponseValue(param).toString();
        Assert.assertEquals(valueFromParam, expectedValue.toString());
        return this;
    }

    public ResponseValidator verifyResponseValueFromObject(String arrayParam, String param, Object expectedValue) {
        try{
            String actualValue = getResponseValueFromJsonObject(arrayParam, param).toString();
            Assert.assertEquals(actualValue, expectedValue);
        } catch (NullPointerException e){
            Assert.assertNull(expectedValue);
        }
        return this;
    }

    public ResponseValidator verifyResponseValueEmpty(String param) {
        String valueFromParam = response.jsonPath().get(param);
        Assert.assertTrue(valueFromParam.isEmpty());
        return this;
    }

    public ResponseValidator verifyResponseValueEmpty(String arrayParam, String param) {
        Object valueFromParam = getResponseValueFromJsonObject(arrayParam, param);
        Assert.assertEquals(valueFromParam, EMPTY_STRING);
        return this;
    }

    public ResponseValidator verifyResponseValueNotEmpty(String param) {
        Object valueFromParam = getResponseValue(param);
        Assert.assertNotEquals(valueFromParam, EMPTY_STRING);
        return this;
    }

    public ResponseValidator verifyResponseValueExists(String param){
        Assert.assertTrue(response.getBody().asString().contains(param));
        return this;
    }

    public ResponseValidator verifyResponseValueNotExists(String param){
        Assert.assertFalse(response.getBody().asString().contains(param));
        return this;
    }

    public ResponseValidator verifyResponseValueNotEmpty(String arrayParam, String param) {
        Object valueFromParam = getResponseValueFromJsonObject(arrayParam, param);
        Assert.assertNotEquals(valueFromParam, EMPTY_STRING);
        return this;
    }

    public ResponseValidator verifyStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
        return this;
    }

    public Object getResponseValue(String param) {
        return response.jsonPath().get(param);
    }

    private Object getResponseValueFromJsonObject(String objectParam, String param) {
        Map<String, Object> arrayParameter = response.jsonPath().get(objectParam);
        return arrayParameter.get(param);
    }


    public ResponseValidator verifyTimeOfCreation(String actualTime, String expectedTime) {
        long actual = Long.parseLong(actualTime);
        long expected = Long.parseLong(expectedTime);

        try {
            Assert.assertEquals(actual, expected);
        } catch (AssertionError e) {
            Assert.assertEquals(actual, expected - 1L);
        }

        return this;
    }

    private void printResponseBody() {
        System.out.println(response.body().asPrettyString());
    }

}
