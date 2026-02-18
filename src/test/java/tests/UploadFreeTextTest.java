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

    @Test(description = "Returns 401 when upload-free-text is called with an invalid API key")
    public void uploadFreeTextUnauthorized() {
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

    /**
     * N5 bug report check.
     * Special characters should not break matching.
     */
    @Test(description = "Handles accents and quotes in free text without returning empty matches")
    public void uploadFreeTextSpecialCharactersShouldStillMatchProductsTest() {
        UploadFreeTextRequest accentedTextBody = new UploadFreeTextRequest()
                .text("Café & Restaurant Supplies")
                .threshold(0.5)
                .topK(3);

        UploadFreeTextRequest quotedMeasurementBody = new UploadFreeTextRequest()
                .text("24\" x 18\" board")
                .threshold(0.5)
                .topK(3);

        uploadFreeText(Constants.API_KEY, accentedTextBody)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected non-empty matches for accent-insensitive text: 'Café & Restaurant Supplies'");

        uploadFreeText(Constants.API_KEY, quotedMeasurementBody)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected non-empty matches for quoted measurement text: '24\" x 18\" board'");
    }

}
