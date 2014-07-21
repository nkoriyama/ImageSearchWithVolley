package org.nkoriyama.imagesearchwithvolley.model;

import com.google.gson.annotations.SerializedName;

public class BingPhotoResponse {
    @SerializedName(value = "d")
    SearchResponse d;

    public boolean isOK() {
        return (d != null && d.results != null);
    }

    public SearchResponse.Result[] getPhotoInfoList() {
        return d.results;
    }

    public boolean hasNext() {
        return (d.next != null);
    }

    class SearchResponse {
        @SerializedName(value = "__next")
        String next;
        @SerializedName(value = "results")
        Result results[];

        class Result implements PhotoInfo {
            @SerializedName(value = "ContentType")
            String contentType;
            @SerializedName(value = "DisplayUrl")
            String displayUrl;
            @SerializedName(value = "FileSize")
            int fileSize;
            @SerializedName(value = "Height")
            int height;
            @SerializedName(value = "ID")
            String id;
            @SerializedName(value = "MediaUrl")
            String mediaUrl;
            @SerializedName(value = "SourceUrl")
            String sourceUrl;
            @SerializedName(value = "Thumbnail")
            Thumbnail thumbnail;
            @SerializedName(value = "Title")
            String title;
            @SerializedName(value = "Width")
            int width;

            @Override
            public String getImageUrl() {
                return mediaUrl;
            }

            @Override
            public String getThumbnailUrl() {
                return thumbnail.mediaUrl;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getShareSubject() {
                String shareSubject;
                shareSubject = "[ImageSearchWithVolley] " +
                        ((title.length() > 60) ? title.substring(0, 60) : title);
                return shareSubject;
            }

            @Override
            public String getShareText() {
                String shareText = "Bing";
                shareText += System.getProperty("line.separator") + "Title:[" + title + "]";
                shareText += System.getProperty("line.separator") + "Image URL:[" + getImageUrl() + "]";
                shareText += System.getProperty("line.separator") + "Page URL:[" + sourceUrl + "]";
                return shareText;
            }

            class Thumbnail {
                @SerializedName(value = "ContentType")
                String contentType;
                @SerializedName(value = "FileSize")
                int fileSize;
                @SerializedName(value = "Height")
                int height;
                @SerializedName(value = "MediaUrl")
                String mediaUrl;
                @SerializedName(value = "Width")
                int width;
            }
        }
    }
}
