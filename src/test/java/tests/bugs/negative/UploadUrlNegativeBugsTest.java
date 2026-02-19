package tests.bugs.negative;

import baseApi.BaseApiTest;
import baseApi.constants.Constants;
import baseApi.models.request.UploadUrlRequest;
import org.testng.annotations.Test;

import static baseApi.constants.ErrorMessages.PLEASE_PROVIDE_VALID_URL;

public class UploadUrlNegativeBugsTest extends BaseApiTest {

    /**
     * N2 bug report check
     * Test will fail because the message is not as described in the document
     */
    @Test(description = "Returns 400 if the url is invalid")
    public void uploadUrlInvalidUrlTest() {
        String invalidUrl = "setvi.com";

        UploadUrlRequest body = new UploadUrlRequest(invalidUrl, 3, 0.5);

        uploadUrl(Constants.API_KEY, body)
                .verifyStatusCode(400)
                .verifyMessageEquals(PLEASE_PROVIDE_VALID_URL);
    }

    @Test(description = "Returns 200 if the url is valid")
    public void uploadUrlValidUrlTest() {
        String validUrl = "https://www.setvi.com";

        UploadUrlRequest body = new UploadUrlRequest(validUrl, 3, 0.5);

        uploadUrl(Constants.API_KEY, body)
                .verifyStatusCode(200);
    }

    /**
     * N4 bug report check
     * topK parameter should define number of returned products
     */
    @Test(description = "Respects topK value when matching URL products")
    public void uploadUrlTopKRespectedTest() {
        String validUrl = "https://www.webstaurantstore.com/search/container.html";

        UploadUrlRequest bodyTopKOne = new UploadUrlRequest(validUrl, 1, 0.5);

        UploadUrlRequest bodyTopKTen = new UploadUrlRequest(validUrl, 10, 0.5);

        uploadUrl(Constants.API_KEY, bodyTopKOne)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountLessThanOrEqual(1, "topK=1 should return up to 1 product");
        uploadUrl(Constants.API_KEY, bodyTopKTen)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountLessThanOrEqual(10, "topK=10 should return up to 10 products");
    }

}
