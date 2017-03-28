package org.nkoriyama.imagesearchwithvolley.model;

import com.google.common.base.Strings;
import com.squareup.moshi.Json;

import java.util.List;

public class FlickrPhotoResponse {
    Photos photos;
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
        String page;
        String pages;
        String perpage;
        List<Photo> photo;
        String total;

        static class Photo implements PhotoInfo {
            String farm;
            String id;
            String isfamily;
            String isfriend;
            String ispublic;
            String owner;
            String secret;
            String server;
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
                        ((!Strings.isNullOrEmpty(title) && title.length() > 60) ? title.substring(0, 60) : title);
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
