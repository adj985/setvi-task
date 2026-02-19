package tests.bugs.negative;

import baseApi.BaseApiTest;
import baseApi.constants.Constants;
import baseApi.models.request.UploadFreeTextRequest;
import org.testng.annotations.Test;

import static baseApi.constants.ErrorMessages.*;

public class UploadFreeTextNegativeBugsTest extends BaseApiTest {

    /**
     * N1 bug report check
     */
    @Test(description = " Returns 400 if the text is empty with the appropriate message")
    public void uploadFreeTextEmptyTextTest() {
        String emptyText = "";

        UploadFreeTextRequest body = new UploadFreeTextRequest(emptyText, 3, 0.5, false);

        uploadFreeText(Constants.API_KEY, body)
                .verifyStatusCode(400)
                .verifyMessageEquals(MUST_NOT_BE_NULL_OR_WHITESPACE);

    }

    @Test(description = "Returns 401 when upload-free-text is called with an invalid API key")
    public void uploadFreeTextUnauthorized() {
        String invalidApiKey = "invalid_api_key";
        String someText = "board";

        UploadFreeTextRequest body = new UploadFreeTextRequest(someText, 3, 0.5, false);

        uploadFreeText(invalidApiKey, body)
                .verifyStatusCode(401);

    }

    /**
     * N4 bug report check
     * topK parameter should define number of returned products
     */
    @Test(description = "Respects topK value when matching free text products")
    public void uploadFreeTextTopKRespectedTest() {
        String someText = "board";

        UploadFreeTextRequest bodyTopKOne = new UploadFreeTextRequest(someText, 1, 0.5, false);
        UploadFreeTextRequest bodyTopKTen = new UploadFreeTextRequest(someText, 10, 0.5, false);

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
        String accentedText = "Café & Restaurant Supplies";
        String quotedMeasurementText = "24\" x 18\" board";

        UploadFreeTextRequest accentedTextBody = new UploadFreeTextRequest(accentedText, 3, 0.5, null);
        UploadFreeTextRequest quotedMeasurementBody = new UploadFreeTextRequest(quotedMeasurementText, 3, 0.5, null);

        uploadFreeText(Constants.API_KEY, accentedTextBody)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected non-empty matches for accent-insensitive text: '" + accentedText + "'");

        uploadFreeText(Constants.API_KEY, quotedMeasurementBody)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected non-empty matches for quoted measurement text: '" + quotedMeasurementText + "'");
    }

}
