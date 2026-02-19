package tests.bugs.positive;

import baseApi.BaseApiTest;
import baseApi.ResponseHelper;
import baseApi.constants.Constants;
import baseApi.models.request.UploadFreeTextRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UploadFreeTextPositiveBugsTest extends BaseApiTest {

    /**
     * P1 bug report check.
     * When private label ranking is enabled, result order should change to prioritize private label products.
     */
    @Test(description = "Private label ranking should change result order when enablePrivateLabelRanking=true")
    public void uploadFreeTextPrivateLabelRankingShouldChangeResultOrderTest() {
        String text = "cutting board";

        UploadFreeTextRequest rankingDisabledRequest = new UploadFreeTextRequest(text, 10, 0.5, false);
        UploadFreeTextRequest rankingEnabledRequest = new UploadFreeTextRequest(text, 10, 0.5, true);

        ResponseHelper rankingDisabledResponse = uploadFreeText(Constants.API_KEY, rankingDisabledRequest)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected non-empty matches when private label ranking is disabled");

        uploadFreeText(Constants.API_KEY, rankingEnabledRequest)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected non-empty matches when private label ranking is enabled")
                .verifyMatchedInternalProductsOrderDifferentFrom(rankingDisabledResponse,
                        "Expected different product order when enablePrivateLabelRanking=true");
    }

    /**
     * P2 bug report check.
     * Query-relevant products should rank first with high similarity.
     */
    @Test(description = "Top result for cutting board query should be relevant and highly similar")
    public void uploadFreeTextTopResultShouldBeRelevantForCuttingBoardQueryTest() {
        String text = "Plastic Cutting Board 24x18";
        UploadFreeTextRequest request = new UploadFreeTextRequest(text, 3, 0.5, false);

        uploadFreeText(Constants.API_KEY, request)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected non-empty matches for cutting board query")
                .verifyFirstMatchedInternalProductNameContains("board",
                        "Expected top match to be a cutting-board related product")
                .verifyFirstMatchedInternalProductNameNotContains("spoon",
                        "Unexpected irrelevant spoon product at top rank")
                .verifyFirstMatchedInternalProductSimilarityScoreAtLeast(0.8,
                        "Expected top cutting-board match to have similarityScore >= 0.8");
    }

    /**
     * P4 bug report check.
     * Same request should produce stable similarity score (deterministic scoring).
     */
    @Test(description = "Same free-text request should produce consistent similarity score across consecutive runs")
    public void uploadFreeTextSimilarityScoreShouldBeDeterministicForSameInputTest() {
        UploadFreeTextRequest request = new UploadFreeTextRequest("Green Polyethylene Board", 1, null, null);

        double scoreAttempt1 = uploadFreeText(Constants.API_KEY, request)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected at least one matched product for deterministic score check")
                .getFirstMatchedInternalProductSimilarityScoreValue();

        double scoreAttempt2 = uploadFreeText(Constants.API_KEY, request)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected at least one matched product for deterministic score check")
                .getFirstMatchedInternalProductSimilarityScoreValue();

        double scoreAttempt3 = uploadFreeText(Constants.API_KEY, request)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected at least one matched product for deterministic score check")
                .getFirstMatchedInternalProductSimilarityScoreValue();

        double maxScore = Math.max(scoreAttempt1, Math.max(scoreAttempt2, scoreAttempt3));
        double minScore = Math.min(scoreAttempt1, Math.min(scoreAttempt2, scoreAttempt3));
        double variation = maxScore - minScore;

        Assert.assertTrue(
                variation <= 0.01,
                "Expected deterministic similarity score variation <= 0.01, but got " + variation
                        + " (attempts: " + scoreAttempt1 + ", " + scoreAttempt2 + ", " + scoreAttempt3 + ")"
        );
    }

    /**
     * P5 bug report check.
     * Matched product should include core commerce metadata fields.
     */
    @Test(description = "First matched product should contain price, sku, vendor, inStock and imageUrl")
    public void uploadFreeTextFirstMatchedProductShouldContainCoreMetadataTest() {
        UploadFreeTextRequest request = new UploadFreeTextRequest("Cutting Board", 3, 0.5, false);

        uploadFreeText(Constants.API_KEY, request)
                .verifyStatusCode(200)
                .verifyMatchedInternalProductsIdCountGreaterThan(0,
                        "Expected at least one matched product for metadata validation")
                .verifyFirstMatchedInternalProductHasCoreMetadata(
                        "Spec mismatch: first matched product should include core metadata fields");
    }
}
