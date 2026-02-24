package baseApi;

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

	private static final String[] MESSAGE_PATHS = {"Message", "message", "error.message", "errors[0].message"};

	private final Response response;

	public ResponseHelper(Response response) {
		this.response = response;
		// printResponseBody();
	}

	public ResponseHelper verifyStatusCode(int statusCode) {
		Assert.assertEquals(response.getStatusCode(), statusCode);
		return this;
	}

	public ResponseHelper verifyMessageEquals(String expectedMessage) {
		String actualMessage = getMessage();
		String responseBody = response.body()
				.asString();

		Assert.assertNotNull(actualMessage,
				String.format("Could not extract message from response using known paths. Response body: %s",
						responseBody));
		Assert.assertEquals(actualMessage, expectedMessage,
				String.format("Unexpected error message. Response body: %s", responseBody));
		return this;
	}

	public ResponseHelper verifyMatchedInternalProductsIdCountEquals(long expectedCount) {
		Assert.assertEquals(getMatchedInternalProductsIdCount(), expectedCount,
				String.format("Expected matchedInternalProducts id count to equal %d", expectedCount));
		return this;
	}

	public ResponseHelper verifyMatchedInternalProductsIdCountLessThanOrEqual(long maxCount) {
		Assert.assertTrue(getMatchedInternalProductsIdCount() <= maxCount,
				String.format("Expected matchedInternalProducts id count to be <= %d", maxCount));
		return this;
	}

	public ResponseHelper verifyMatchedInternalProductsIdCountGreaterThan(long minCount) {
		Assert.assertTrue(getMatchedInternalProductsIdCount() > minCount,
				String.format("Expected matchedInternalProducts id count to be > %d", minCount));
		return this;
	}

	public ResponseHelper verifyMatchedInternalProductsOrderDifferentFrom(ResponseHelper other) {
		List<String> currentOrder = getMatchedInternalProductIdsInOrder();
		List<String> otherOrder = other.getMatchedInternalProductIdsInOrder();

		Assert.assertFalse(currentOrder.isEmpty() && otherOrder.isEmpty(),
				"Both responses returned empty matched product IDs.");
		Assert.assertNotEquals(currentOrder, otherOrder,
				"Expected a different order of matchedInternalProducts between responses");
		return this;
	}

	public ResponseHelper verifyFirstMatchedInternalProductNameContains(String expectedSubstring) {
		String productName = getFirstMatchedInternalProductName();
		Assert.assertTrue(productName.toLowerCase()
				.contains(expectedSubstring.toLowerCase()),
				String.format("Expected first matched product name to contain '%s'. First product name: '%s'",
						expectedSubstring, productName));
		return this;
	}

	public ResponseHelper verifyFirstMatchedInternalProductNameNotContains(String unexpectedSubstring) {
		String productName = getFirstMatchedInternalProductName();
		Assert.assertFalse(productName.toLowerCase()
				.contains(unexpectedSubstring.toLowerCase()),
				String.format("Expected first matched product name not to contain '%s'. First product name: '%s'",
						unexpectedSubstring, productName));
		return this;
	}

	public ResponseHelper verifyFirstMatchedInternalProductHasCoreMetadata() {
		MatchedInternalProductResponse firstProduct = getFirstMatchedInternalProduct();

		Assert.assertNotNull(firstProduct._id, "Missing field: _id");
		Assert.assertFalse(firstProduct._id.isBlank(), "Field is blank: _id");
		Assert.assertNotNull(firstProduct.sku, "Missing field: sku");
		Assert.assertFalse(firstProduct.sku.isBlank(), "Field is blank: sku");

		String vendorName = getVendorName(firstProduct.vendor);
		Assert.assertNotNull(vendorName, "Missing vendor name");
		Assert.assertFalse(vendorName.isBlank(), "Field is blank: vendor name");

		String imagePath = getPrimaryImagePath(firstProduct);
		Assert.assertNotNull(imagePath, "Missing product image path");
		Assert.assertFalse(imagePath.isBlank(), "Field is blank: product image path");

		// Current schema does not return price/inStock; percentage is the closest
		// quality signal.
		Assert.assertNotNull(firstProduct.percentage, "Missing field: percentage");
		Assert.assertTrue(firstProduct.percentage >= 0 && firstProduct.percentage <= 100,
				String.format("Invalid percentage value: %s", firstProduct.percentage));

		return this;
	}

	private String getMessage() {
		for (String path : MESSAGE_PATHS) {
			String value = response.jsonPath()
					.getString(path);
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return null;
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
		Assert.assertNotNull(productName, String.format(
				"Spec mismatch: first matched product does not contain '%s' or '%s'.",
				ItemsPath.PRODUCT_NAME, ItemsPath.NAME));
		return productName;
	}

	private List<MatchedInternalProductResponse> getMatchedInternalProductsFromFirstResultItem() {
		List<MatchedItemResponse> matchedItems = getMatchedItems();
		if (matchedItems.isEmpty()) {
			Assert.fail("Could not find matchedItems in response");
			return Collections.emptyList();
		}
		MatchedItemResponse firstMatchedItem = matchedItems.getFirst();
		return Objects.requireNonNullElse(firstMatchedItem.matchedInternalProducts, Collections.emptyList());
	}

	private List<MatchedItemResponse> getMatchedItems() {
		List<MatchedItemResponse> matchedItems = response.jsonPath()
				.getList(ItemsPath.MATCHED_ITEMS,
				MatchedItemResponse.class);
		return Objects.requireNonNullElse(matchedItems, Collections.emptyList());
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
		return switch (vendor) {
			case null -> null;
			case String vendorValue -> vendorValue;
			case Map<?, ?> vendorMap -> {
				Object name = vendorMap.get("name");
				yield name == null ? null : name.toString();
			}
			default -> vendor.toString();
		};
	}

}
