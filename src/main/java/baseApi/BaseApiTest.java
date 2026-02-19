package baseApi;

import baseApi.constants.Constants;
import baseApi.constants.Endpoints;
import baseApi.models.request.UploadFreeTextRequest;
import baseApi.models.request.UploadUrlRequest;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class BaseApiTest extends RestAssured {

    private final Gson gson = new Gson();

    protected ResponseHelper uploadFreeText(String apiKey, UploadFreeTextRequest requestBody) {
        return postRequest(Endpoints.UPLOAD_FREE_TEXT, apiKey, requestBody);
    }

    protected ResponseHelper uploadUrl(String apiKey, UploadUrlRequest requestBody) {
        return postRequest(Endpoints.UPLOAD_URL_HTML, apiKey, requestBody);
    }

    private ResponseHelper postRequest(String endpoint, String apiKey, Object requestBody) {

        return new ResponseHelper(
                given()
                        .spec(buildRequestSpec(apiKey))
                        .body(gson.toJson(requestBody))
                        .when()
                        .post(endpoint));
    }

    private RequestSpecification buildRequestSpec(String apiKey) {
        return new RequestSpecBuilder()
                .setBaseUri(Constants.BASE_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", apiKey)
                .build();
    }

}
