package tests;

import baseApi.BaseApiTest;
import baseApi.BodyParams;
import baseApi.Constants;
import baseApi.ErrorMessages;
import com.google.gson.JsonObject;
import org.testng.annotations.Test;

import static baseApi.BodyParams.*;
import static baseApi.Endpoints.UPLOAD_URL_HTML;

public class UploadUrlTest extends BaseApiTest {

    /**
     * N2 bug report check
     * Test will fail because the message is not as described in the document
     */
    @Test(description = "Returns 400 if the url is invalid")
    public void uploadUrlInvalidUrlTest(){

        String invalidURL = "setvi.com";

        JsonObject body = new JsonObject();
        body.addProperty(URL, invalidURL);
        body.addProperty(TOP_K, 3);
        body.addProperty(THRESHOLD, 0.5);

        postRequest(UPLOAD_URL_HTML, Constants.API_KEY, body)
                .verifyStatusCode(400)
                .verifyResponseValue(BodyParams.MESSAGE, ErrorMessages.PLEASE_PROVIDE_VALID_URL);
    }

    @Test(description = "Returns 200 if the url is valid")
    public void uploadUrlValidUrlTest(){

        String validURL = "https://www.setvi.com";

        JsonObject body = new JsonObject();
        body.addProperty(URL, validURL);
        body.addProperty(BodyParams.TOP_K, 3);
        body.addProperty(BodyParams.THRESHOLD, 0.5);

        postRequest(UPLOAD_URL_HTML, Constants.API_KEY, body)
                .verifyStatusCode(200);
    }

}
