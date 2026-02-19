package baseApi;

import baseApi.constants.BodyParams;
import baseApi.constants.ItemsPath;
import baseApi.model.response.MatchedInternalProductResponse;
import baseApi.model.response.MatchedItemResponse;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResponseValidator {

    private final Response response;

    public ResponseValidator(Response response) {
        this.response = response;
    }

    public ResponseValidator verifyStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
        return this;
    }

    public ResponseValidator verifyMessageEquals(String expectedMessage) {
        Assert.assertEquals(getResponseValue(BodyParams.MESSAGE).toString(), expectedMessage);
        return this;
    }

    public ResponseValidator verifyMatchedInternalProductsIdCountEquals(long expectedCount, String message) {
        Assert.assertEquals(getMatchedInternalProductsIdCount(), expectedCount, message);
        return this;
    }

    public ResponseValidator verifyMatchedInternalProductsIdCountLessThanOrEqual(long maxCount, String message) {
        Assert.assertTrue(getMatchedInternalProductsIdCount() <= maxCount, message);
        return this;
    }

    public ResponseValidator verifyMatchedInternalProductsIdCountGreaterThan(long minCount, String message) {
        Assert.assertTrue(getMatchedInternalProductsIdCount() > minCount, message);
        return this;
    }

    public ResponseValidator verifyMatchedInternalProductsOrderDifferentFrom(ResponseValidator other, String message) {
        List<String> currentOrder = getMatchedInternalProductIdsInOrder();
        List<String> otherOrder = other.getMatchedInternalProductIdsInOrder();

        Assert.assertFalse(currentOrder.isEmpty() && otherOrder.isEmpty(),
                "Both responses returned empty matched product IDs. " + message);
        Assert.assertNotEquals(currentOrder, otherOrder, message);
        return this;
    }

    public ResponseValidator verifyFirstMatchedInternalProductNameContains(String expectedSubstring, String message) {
        String productName = getFirstMatchedInternalProductName();
        Assert.assertTrue(
                productName.toLowerCase().contains(expectedSubstring.toLowerCase()),
                message + ". First product name: '" + productName + "'"
        );
        return this;
    }

    public ResponseValidator verifyFirstMatchedInternalProductNameNotContains(String unexpectedSubstring, String message) {
        String productName = getFirstMatchedInternalProductName();
        Assert.assertFalse(
                productName.toLowerCase().contains(unexpectedSubstring.toLowerCase()),
                message + ". First product name: '" + productName + "'"
        );
        return this;
    }

    public ResponseValidator verifyFirstMatchedInternalProductSimilarityScoreAtLeast(double minScore, String message) {
        double score = getFirstMatchedInternalProductSimilarityScore();
        Assert.assertTrue(
                score >= minScore,
                message + ". First product similarityScore: " + score
        );
        return this;
    }

    public double getFirstMatchedInternalProductSimilarityScoreValue() {
        return getFirstMatchedInternalProductSimilarityScore();
    }

    private <T> T getResponseValue(String param) {
        return response.jsonPath().get(param);
    }

    private long getMatchedInternalProductsIdCount() {
        List<MatchedInternalProductResponse> matchedInternalProducts = getMatchedInternalProductsFromFirstResultItem();
        return matchedInternalProducts.stream()
                .filter(product -> product._id != null)
                .count();
    }

    private List<String> getMatchedInternalProductIdsInOrder() {
        List<MatchedInternalProductResponse> matchedInternalProducts = getMatchedInternalProductsFromFirstResultItem();
        return matchedInternalProducts.stream()
                .map(product -> product._id)
                .filter(id -> id != null)
                .collect(Collectors.toList());
    }

    private MatchedInternalProductResponse getFirstMatchedInternalProduct() {
        List<MatchedInternalProductResponse> matchedInternalProducts = getMatchedInternalProductsFromFirstResultItem();
        Assert.assertFalse(matchedInternalProducts.isEmpty(), "No matchedInternalProducts found in first matched item");
        return matchedInternalProducts.getFirst();
    }

    private String getFirstMatchedInternalProductName() {
        MatchedInternalProductResponse firstProduct = getFirstMatchedInternalProduct();
        String productName = firstProduct.productName;
        if (productName == null) {
            productName = firstProduct.name;
        }
        Assert.assertNotNull(productName,
                "Spec mismatch: first matched product does not contain '" + ItemsPath.PRODUCT_NAME + "' or '"
                        + ItemsPath.NAME + "'.");
        return productName;
    }

    private double getFirstMatchedInternalProductSimilarityScore() {
        MatchedInternalProductResponse firstProduct = getFirstMatchedInternalProduct();
        Double similarityScore = firstProduct.similarityScore;
        Assert.assertNotNull(firstProduct.similarityScore,
                "Spec mismatch: first matched product does not contain '" + ItemsPath.SIMILARITY_SCORE + "'.");
        return similarityScore;
    }

    private List<MatchedInternalProductResponse> getMatchedInternalProductsFromFirstResultItem() {
        MatchedItemResponse firstMatchedItem = getFirstResultItem(ItemsPath.MATCHED_ITEMS, MatchedItemResponse.class);
        if (firstMatchedItem == null) {
            Assert.fail("Could not find matchedItems in response");
            return Collections.emptyList();
        }
        if (firstMatchedItem.matchedInternalProducts == null) {
            return Collections.emptyList();
        }
        return firstMatchedItem.matchedInternalProducts;
    }

    private <T> T getFirstResultItem(String itemsPath, Class<T> itemClass) {
        List<T> items = response.jsonPath().getList(itemsPath, itemClass);
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.getFirst();
    }

    private void printResponseBody() {
        System.out.println(response.body().asPrettyString());
    }

}
