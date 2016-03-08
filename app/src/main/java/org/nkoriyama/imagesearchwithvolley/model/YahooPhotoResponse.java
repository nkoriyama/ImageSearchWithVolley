package org.nkoriyama.imagesearchwithvolley.model;

import com.google.common.base.Strings;
import com.squareup.moshi.Json;

import java.util.List;

public class YahooPhotoResponse {
    @Json(name = "bossresponse")
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
        @Json(name = "responsecode")
        String responsecode;
        @Json(name = "images")
        Images images;

        static class Images {
            @Json(name = "start")
            String start;
            @Json(name = "count")
            String count;
            @Json(name = "totalresults")
            String totalresults;
            @Json(name = "results")
            List<Result> results;


            static class Result implements PhotoInfo {
                @Json(name = "clickurl")
                String clickurl;
                @Json(name = "size")
                String size;
                @Json(name = "format")
                String format;
                @Json(name = "height")
                String height;
                @Json(name = "refererclickurl")
                String refererclickurl;
                @Json(name = "refererurl")
                String refererurl;
                @Json(name = "title")
                String title;
                @Json(name = "url")
                String url;
                @Json(name = "width")
                String width;
                @Json(name = "thumbnailurl")
                String thumbnailurl;
                @Json(name = "thumbnailheight")
                String thumbnailheight;
                @Json(name = "thumbnailwidth")
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
