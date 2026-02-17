package apiTests;

import baseApi.BaseApiTest;
import baseApi.BodyParams;
import baseApi.Constants;
import com.google.gson.JsonObject;
import org.testng.annotations.Test;

import static baseApi.BodyParams.*;
import static baseApi.Endpoints.UPLOAD_FREE_TEXT;
import static baseApi.ErrorMessages.*;

public class UploadFreeTextTest extends BaseApiTest {

    /**
     * N1 bug report check
     */
    @Test(description = " Returns 400 if the text is empty with the appropriate message")
    public void uploadFreeTextEmptyTextTest() {

        JsonObject body = new JsonObject();
        body.addProperty(TEXT, "");
        body.addProperty(TOP_K, 3);
        body.addProperty(THRESHOLD, 0.5);
        body.addProperty(ENABLE_PRIVATE_LABEL_RANKING, false);

        postRequest(UPLOAD_FREE_TEXT, Constants.API_KEY, body)
                .verifyStatusCode(400)
                .verifyResponseValue(MESSAGE, MUST_NOT_BE_NULL_OR_WHITESPACE);

    }

    @Test(description = "")
    public void uploadFreeTextUnauthorized(){

        String invalidApiKey = "a8f9a8f-a9sya9sf78";

        JsonObject body = new JsonObject();
        body.addProperty(BodyParams.TEXT, "Some text");
        body.addProperty(BodyParams.TOP_K, 3);
        body.addProperty(BodyParams.THRESHOLD, 0.5);
        body.addProperty(BodyParams.ENABLE_PRIVATE_LABEL_RANKING, false);

        postRequest(UPLOAD_FREE_TEXT, invalidApiKey, body)
                .verifyStatusCode(401);

    }

}
