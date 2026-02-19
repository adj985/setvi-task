package baseApi.model.request;

import com.google.gson.annotations.SerializedName;

public record UploadFreeTextRequest(
        @SerializedName("text") String text,
        @SerializedName("topK") Integer topK,
        @SerializedName("threshold") Double threshold,
        @SerializedName("enablePrivateLabelRanking") Boolean enablePrivateLabelRanking
) {
}
