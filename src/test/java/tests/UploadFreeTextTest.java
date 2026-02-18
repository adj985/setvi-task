package tests;

import baseApi.BaseApiTest;
import baseApi.Constants;
import baseApi.model.UploadFreeTextRequest;
import org.testng.annotations.Test;

import static baseApi.ErrorMessages.*;

public class UploadFreeTextTest extends BaseApiTest {

    /**
     * N1 bug report check
     */
    @Test(description = " Returns 400 if the text is empty with the appropriate message")
    public void uploadFreeTextEmptyTextTest() {

        UploadFreeTextRequest body = new UploadFreeTextRequest()
                .text("")
                .topK(3)
                .threshold(0.5)
                .enablePrivateLabelRanking(false);

        uploadFreeText(Constants.API_KEY, body)
                .verifyStatusCode(400)
                .verifyMessageEquals(MUST_NOT_BE_NULL_OR_WHITESPACE);

    }

    @Test(description = "")
    public void uploadFreeTextUnauthorized(){

        String invalidApiKey = "a8f9a8f-a9sya9sf78";

        UploadFreeTextRequest body = new UploadFreeTextRequest()
                .text("Some text")
                .topK(3)
                .threshold(0.5)
                .enablePrivateLabelRanking(false);

        uploadFreeText(invalidApiKey, body)
                .verifyStatusCode(401);

    }

    /**
     * N4 bug report check
     * topK parameter should define number of returned products
     */
    @Test(description = "Respects topK value when matching free text products")
    public void uploadFreeTextTopKRespectedTest() {
        UploadFreeTextRequest bodyTopKOne = new UploadFreeTextRequest()
                .text("Some text")
                .topK(1)
                .threshold(0.5)
                .enablePrivateLabelRanking(false);

        UploadFreeTextRequest bodyTopKTen = new UploadFreeTextRequest()
                .text("Some text")
                .topK(10)
                .threshold(0.5)
                .enablePrivateLabelRanking(false);

        uploadFreeText(Constants.API_KEY, bodyTopKOne)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountEquals(1,
                        "topK=1 should return exactly 1 product");
        uploadFreeText(Constants.API_KEY, bodyTopKTen)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountLessThanOrEqual(10,
                        "topK=10 should return up to 10 products");
    }

}
