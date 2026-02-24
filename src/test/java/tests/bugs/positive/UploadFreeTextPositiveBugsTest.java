package tests.bugs.positive;

import baseApi.BaseApiTest;
import baseApi.ResponseHelper;
import baseApi.constants.Constants;
import baseApi.models.request.UploadFreeTextRequest;
import org.testng.annotations.Test;

public class UploadFreeTextPositiveBugsTest extends BaseApiTest {

	/**
	 * P1 bug report check. When private label ranking is enabled, result order
	 * should change to prioritize private label products.
	 */
	@Test(description = "Private label ranking should change result order when enablePrivateLabelRanking=true")
	public void uploadFreeTextPrivateLabelRankingShouldChangeResultOrderTest() {
		String text = "cutting board";

		UploadFreeTextRequest rankingDisabledRequest = new UploadFreeTextRequest(text, 10, 0.5, false);
		UploadFreeTextRequest rankingEnabledRequest = new UploadFreeTextRequest(text, 10, 0.5, true);

		ResponseHelper rankingDisabledResponse = uploadFreeText(Constants.API_KEY, rankingDisabledRequest)
				.verifyStatusCode(200).verifyMatchedInternalProductsIdCountGreaterThan(0);

		uploadFreeText(Constants.API_KEY, rankingEnabledRequest).verifyStatusCode(200)
				.verifyMatchedInternalProductsIdCountGreaterThan(0)
				.verifyMatchedInternalProductsOrderDifferentFrom(rankingDisabledResponse);
	}

	/**
	 * P2 bug report check. Query-relevant products should rank first with high
	 * similarity.
	 */
	@Test(description = "Top result for cutting board query should be relevant and highly similar")
	public void uploadFreeTextTopResultShouldBeRelevantForCuttingBoardQueryTest() {
		String text = "Plastic Cutting Board 24x18";
		UploadFreeTextRequest request = new UploadFreeTextRequest(text, 3, 0.5, false);

		uploadFreeText(Constants.API_KEY, request).verifyStatusCode(200)
				.verifyMatchedInternalProductsIdCountGreaterThan(0).verifyFirstMatchedInternalProductNameContains("board")
				.verifyFirstMatchedInternalProductNameNotContains("spoon");
	}

	/**
	 * P5 bug report check. Matched product should include core metadata fields for
	 * current response schema.
	 */
	@Test(description = "First matched product should contain _id, sku, vendor name, image path and percentage")
	public void uploadFreeTextFirstMatchedProductShouldContainCoreMetadataTest() {
		UploadFreeTextRequest request = new UploadFreeTextRequest("Cutting Board", 3, 0.5, false);

		uploadFreeText(Constants.API_KEY, request).verifyStatusCode(200)
				.verifyMatchedInternalProductsIdCountGreaterThan(0).verifyFirstMatchedInternalProductHasCoreMetadata();
	}
}
