package baseApi.model.request;

import com.google.gson.annotations.SerializedName;

public record UploadUrlRequest(
        @SerializedName("url") String url,
        @SerializedName("topK") Integer topK,
        @SerializedName("threshold") Double threshold
) {
}
