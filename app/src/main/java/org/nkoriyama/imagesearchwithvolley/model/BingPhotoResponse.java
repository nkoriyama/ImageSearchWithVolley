package org.nkoriyama.imagesearchwithvolley.model;

import com.google.common.base.Strings;
import com.squareup.moshi.Json;

import java.util.List;

public class BingPhotoResponse {
    @Json(name = "d")
    SearchResponse d;

    public boolean isOK() {
        return (d != null && d.results != null);
    }

    public List<SearchResponse.Result> getPhotoInfoList() {
        return d.results;
    }

    public boolean hasNext() {
        return (d.next != null);
    }

    static class SearchResponse {
        @Json(name = "__next")
        String next;
        @Json(name = "results")
        List<Result> results;

        static class Result implements PhotoInfo {
            @Json(name = "ContentType")
            String contentType;
            @Json(name = "DisplayUrl")
            String displayUrl;
            @Json(name = "FileSize")
            int fileSize;
            @Json(name = "Height")
            int height;
            @Json(name = "ID")
            String id;
            @Json(name = "MediaUrl")
            String mediaUrl;
            @Json(name = "SourceUrl")
            String sourceUrl;
            @Json(name = "Thumbnail")
            Thumbnail thumbnail;
            @Json(name = "Title")
            String title;
            @Json(name = "Width")
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
                        ((!Strings.isNullOrEmpty(title) && title.length() > 60) ? title.substring(0, 60) : title);
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

            static class Thumbnail {
                @Json(name = "ContentType")
                String contentType;
                @Json(name = "FileSize")
                int fileSize;
                @Json(name = "Height")
                int height;
                @Json(name = "MediaUrl")
                String mediaUrl;
                @Json(name = "Width")
                int width;
            }
        }
    }
}
