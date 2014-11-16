package org.nkoriyama.imagesearchwithvolley.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FlickrPhotoResponse {
    @SerializedName(value = "photos")
    Photos photos;
    @SerializedName(value = "stat")
    String stat;

    public boolean isOK() {
        return (stat != null && stat.equals("ok"));
    }

    public List<Photos.Photo> getPhotoInfoList() {
        return photos.photo;
    }

    public int getTotal() {
        return Integer.parseInt(photos.total);
    }

    static class Photos {
        @SerializedName(value = "page")
        String page;
        @SerializedName(value = "pages")
        String pages;
        @SerializedName(value = "perpage")
        String perpage;
        @SerializedName(value = "photo")
        List<Photo> photo;
        @SerializedName(value = "total")
        String total;

        static class Photo implements PhotoInfo {
            @SerializedName(value = "farm")
            String farm;
            @SerializedName(value = "id")
            String id;
            @SerializedName(value = "isfamily")
            String isfamily;
            @SerializedName(value = "isfriend")
            String isfriend;
            @SerializedName(value = "ispublic")
            String ispublic;
            @SerializedName(value = "owner")
            String owner;
            @SerializedName(value = "secret")
            String secret;
            @SerializedName(value = "server")
            String server;
            @SerializedName(value = "title")
            String title;

            @Override
            public String getImageUrl() {
                return "http://farm" + farm + ".staticflickr.com/" +
                        server + "/" + id + "_" + secret + "_b.jpg";
            }

            @Override
            public String getThumbnailUrl() {
                return "http://farm" + farm + ".staticflickr.com/" +
                        server + "/" + id + "_" + secret + "_m.jpg";
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

            private String getPhotoPageUrl() {
                return "http://www.flickr.com/photos/" + owner + "/" + id;
            }

            @Override
            public String getShareText() {
                String shareText = "Flickr";
                shareText += System.getProperty("line.separator") + "Title:[" + title + "]";
                shareText += System.getProperty("line.separator") + "Image URL:[" + getImageUrl() + "]";
                shareText += System.getProperty("line.separator") + "Page URL:[" + getPhotoPageUrl() + "]";
                return shareText;
            }

        }
    }
}
