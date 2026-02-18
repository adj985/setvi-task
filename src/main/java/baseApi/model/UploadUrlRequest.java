package baseApi.model;

public class UploadUrlRequest {

    private String url;
    private Integer topK;
    private Double threshold;

    public UploadUrlRequest url(String url) {
        this.url = url;
        return this;
    }

    public UploadUrlRequest topK(Integer topK) {
        this.topK = topK;
        return this;
    }

    public UploadUrlRequest threshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }
}
