package baseApi;

import baseApi.constants.BodyParams;
import baseApi.constants.ItemsPath;
import baseApi.models.response.MatchedInternalProductResponse;
import baseApi.models.response.MatchedItemResponse;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResponseHelper {

    private final Response response;

    public ResponseHelper(Response response) {
        this.response = response;
//        printResponseBody();
    }

    public ResponseHelper verifyStatusCode(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
        return this;
    }

    public ResponseHelper verifyMessageEquals(String expectedMessage) {
        Assert.assertEquals(getMessage(), expectedMessage);
        return this;
    }

    public ResponseHelper verifyMatchedInternalProductsIdCountEquals(long expectedCount, String message) {
        Assert.assertEquals(getMatchedInternalProductsIdCount(), expectedCount, message);
        return this;
    }

    public ResponseHelper verifyMatchedInternalProductsIdCountLessThanOrEqual(long maxCount, String message) {
        Assert.assertTrue(getMatchedInternalProductsIdCount() <= maxCount, message);
        return this;
    }

    public ResponseHelper verifyMatchedInternalProductsIdCountGreaterThan(long minCount, String message) {
        Assert.assertTrue(getMatchedInternalProductsIdCount() > minCount, message);
        return this;
    }

    public ResponseHelper verifyMatchedInternalProductsOrderDifferentFrom(ResponseHelper other, String message) {
        List<String> currentOrder = getMatchedInternalProductIdsInOrder();
        List<String> otherOrder = other.getMatchedInternalProductIdsInOrder();

        Assert.assertFalse(currentOrder.isEmpty() && otherOrder.isEmpty(),
                "Both responses returned empty matched product IDs. " + message);
        Assert.assertNotEquals(currentOrder, otherOrder, message);
        return this;
    }

    public ResponseHelper verifyFirstMatchedInternalProductNameContains(String expectedSubstring, String message) {
        String productName = getFirstMatchedInternalProductName();
        Assert.assertTrue(
                productName.toLowerCase().contains(expectedSubstring.toLowerCase()),
                message + ". First product name: '" + productName + "'"
        );
        return this;
    }

    public ResponseHelper verifyFirstMatchedInternalProductNameNotContains(String unexpectedSubstring, String message) {
        String productName = getFirstMatchedInternalProductName();
        Assert.assertFalse(
                productName.toLowerCase().contains(unexpectedSubstring.toLowerCase()),
                message + ". First product name: '" + productName + "'"
        );
        return this;
    }

    public ResponseHelper verifyFirstMatchedInternalProductHasCoreMetadata(String message) {
        MatchedInternalProductResponse firstProduct = getFirstMatchedInternalProduct();

        Assert.assertNotNull(firstProduct._id, message + " Missing field: _id");
        Assert.assertFalse(firstProduct._id.isBlank(), message + " Field is blank: _id");
        Assert.assertNotNull(firstProduct.sku, message + " Missing field: sku");
        Assert.assertFalse(firstProduct.sku.isBlank(), message + " Field is blank: sku");

        String vendorName = getVendorName(firstProduct.vendor);
        Assert.assertNotNull(vendorName, message + " Missing vendor name");
        Assert.assertFalse(vendorName.isBlank(), message + " Field is blank: vendor name");

        String imagePath = getPrimaryImagePath(firstProduct);
        Assert.assertNotNull(imagePath, message + " Missing product image path");
        Assert.assertFalse(imagePath.isBlank(), message + " Field is blank: product image path");

        // Current schema does not return price/inStock; percentage is the closest quality signal.
        Assert.assertNotNull(firstProduct.percentage, message + " Missing field: percentage");
        Assert.assertTrue(
                firstProduct.percentage >= 0 && firstProduct.percentage <= 100,
                message + " Invalid percentage value: " + firstProduct.percentage
        );

        return this;
    }

    private String getMessage() {
        return response.jsonPath().get(BodyParams.MESSAGE);
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
                .filter(Objects::nonNull)
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
        return Objects.requireNonNullElse(firstMatchedItem.matchedInternalProducts, Collections.emptyList());
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

    private String getPrimaryImagePath(MatchedInternalProductResponse product) {
        if (product.imageUrl != null && !product.imageUrl.isBlank()) {
            return product.imageUrl;
        }
        if (product.images == null || product.images.isEmpty() || product.images.getFirst() == null) {
            return null;
        }
        return product.images.getFirst().path;
    }

    private String getVendorName(Object vendor) {
        if (vendor == null) {
            return null;
        }
        if (vendor instanceof String vendorValue) {
            return vendorValue;
        }
        if (vendor instanceof Map<?, ?> vendorMap) {
            Object name = vendorMap.get("name");
            return name == null ? null : name.toString();
        }
        return vendor.toString();
    }

}
