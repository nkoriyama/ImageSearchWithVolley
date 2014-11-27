package org.nkoriyama.imagesearchwithvolley.model;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YahooPhotoResponse {
    @SerializedName(value = "bossresponse")
    BossResponse bossresponse;

    public boolean isOK() {
        return (bossresponse != null && bossresponse.responsecode.equals("200"));
    }

    public List<BossResponse.Images.Result> getPhotoInfoList() {
        return bossresponse.images.results;
    }

    public int getTotal() {
        return Integer.parseInt(bossresponse.images.totalresults);
    }

    static class BossResponse {
        @SerializedName(value = "responsecode")
        String responsecode;
        @SerializedName(value = "images")
        Images images;

        static class Images {
            @SerializedName(value = "start")
            String start;
            @SerializedName(value = "count")
            String count;
            @SerializedName(value = "totalresults")
            String totalresults;
            @SerializedName(value = "results")
            List<Result> results;


            static class Result implements PhotoInfo {
                @SerializedName(value = "clickurl")
                String clickurl;
                @SerializedName(value = "size")
                String size;
                @SerializedName(value = "format")
                String format;
                @SerializedName(value = "height")
                String height;
                @SerializedName(value = "refererclickurl")
                String refererclickurl;
                @SerializedName(value = "refererurl")
                String refererurl;
                @SerializedName(value = "title")
                String title;
                @SerializedName(value = "url")
                String url;
                @SerializedName(value = "width")
                String width;
                @SerializedName(value = "thumbnailurl")
                String thumbnailurl;
                @SerializedName(value = "thumbnailheight")
                String thumbnailheight;
                @SerializedName(value = "thumbnailwidth")
                String thumbnailwidth;

                @Override
                public String getImageUrl() {
                    return url;
                }

                @Override
                public String getThumbnailUrl() {
                    return thumbnailurl;
                }

                @Override
                public String getTitle() {
                    return title;
                }

                @Override
                public String getShareSubject() {
                    String shareSubject;
                    shareSubject = "[ImageSearchWithVolley] " +
                            ((!Strings.isNullOrEmpty(getTitle()) && getTitle().length() > 60) ? getTitle().substring(0, 60) : getTitle());
                    return shareSubject;
                }

                private String getPhotoPageUrl() {
                    return refererurl;
                }

                @Override
                public String getShareText() {
                    String shareText = "Yahoo";
                    final String title = getTitle();
                    if (!Strings.isNullOrEmpty(title)) {
                        shareText += System.getProperty("line.separator") + "Title:[" + title + "]";
                    }
                    final String imageUrl = getImageUrl();
                    if (!Strings.isNullOrEmpty(imageUrl)) {
                        shareText += System.getProperty("line.separator") + "Image URL:[" + imageUrl + "]";
                    }
                    final String photoPageUrl = getPhotoPageUrl();
                    if (!Strings.isNullOrEmpty(photoPageUrl)) {
                        shareText += System.getProperty("line.separator") + "Page URL:[" + photoPageUrl + "]";
                    }
                    return shareText;
                }

            }
        }
    }
}
