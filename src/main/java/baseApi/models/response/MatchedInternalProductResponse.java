package baseApi.models.response;

import java.util.List;

public class MatchedInternalProductResponse {

    public String _id;
    public String name;
    public String productName;
    public String description;
    public Double similarityScore;
    public Double price;
    public String sku;
    public Object vendor;
    public Boolean inStock;
    public String imageUrl;
    public List<ProductImageResponse> images;
    public Integer percentage;
}
