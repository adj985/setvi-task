package baseApi;

import com.google.gson.JsonObject;
import io.restassured.RestAssured;

import java.util.HashMap;
import java.util.Map;

public class BaseApiTest extends RestAssured {

    protected ResponseValidator postRequest(String endpoint, String apiKey,
                                            JsonObject requestBody) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", apiKey);

        return new ResponseValidator(
                given()
                        .headers(headers)
                        .body(requestBody)
                        .when()
                        .post(getInstanceUrl(Constants.BASE_URL, endpoint)));
    }

    private String getInstanceUrl(String baseUrl, String endpoint) {
        return String.format("%s%s", baseUrl , endpoint);
    }

}
