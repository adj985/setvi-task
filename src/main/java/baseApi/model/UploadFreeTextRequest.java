package baseApi.model;

public class UploadFreeTextRequest {

    private String text;
    private Integer topK;
    private Double threshold;
    private Boolean enablePrivateLabelRanking;

    public UploadFreeTextRequest text(String text) {
        this.text = text;
        return this;
    }

    public UploadFreeTextRequest topK(Integer topK) {
        this.topK = topK;
        return this;
    }

    public UploadFreeTextRequest threshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }

    public UploadFreeTextRequest enablePrivateLabelRanking(Boolean enablePrivateLabelRanking) {
        this.enablePrivateLabelRanking = enablePrivateLabelRanking;
        return this;
    }
}
