package baseApi.dto.response;

import java.util.List;

public class MatchedInternalProductResponse {

    public String _id;
    public String name;
    public String productName;
    public String sku;
    public Object vendor;
    public String imageUrl;
    public List<ProductImageResponse> images;
    public Integer percentage;
}
