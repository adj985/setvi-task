package baseApi;

import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResponseValidator {

    private final Response response;

    public ResponseValidator(Response response) {
        this.response = response;
        printResponseBody();
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

    private Object getResponseValue(String param) {
        return response.jsonPath().get(param);
    }

    private long getMatchedInternalProductsIdCount() {
        List<Map<String, Object>> matchedInternalProducts = getMatchedInternalProductsFromFirstResultItem();
        return matchedInternalProducts.stream()
                .filter(product -> product.get("_id") != null)
                .count();
    }

    private List<Map<String, Object>> getMatchedInternalProductsFromFirstResultItem() {
        Map<String, Object> firstMatchedItem = getFirstResultItem("result.matchedItems");
        if (firstMatchedItem != null) {
            return getMatchedInternalProducts(firstMatchedItem);
        }

        Map<String, Object> firstNotMatchedItem = getFirstResultItem("result.notMatchedItems");
        if (firstNotMatchedItem != null) {
            return getMatchedInternalProducts(firstNotMatchedItem);
        }

        Assert.fail("Could not find matchedItems/notMatchedItems in response");
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getMatchedInternalProducts(Map<String, Object> item) {
        Object matchedInternalProducts = item.get("matchedInternalProducts");
        if (matchedInternalProducts == null) {
            return Collections.emptyList();
        }
        return (List<Map<String, Object>>) matchedInternalProducts;
    }

    private Map<String, Object> getFirstResultItem(String itemsPath) {
        List<Map<String, Object>> items = response.jsonPath().getList(itemsPath);
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.getFirst();
    }

    private void printResponseBody() {
        System.out.println(response.body().asPrettyString());
    }

}
