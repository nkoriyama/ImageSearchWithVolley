package org.nkoriyama.imagesearchwithvolley.model;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InstagramPhotoResponse {
    @SerializedName(value = "pagination")
    Pagination pagination;
    @SerializedName(value = "meta")
    Meta meta;
    @SerializedName(value = "data")
    List<ItemData> data;

    public boolean isOK() {
        return (meta != null && meta.code == 200);
    }

    public List<ItemData> getPhotoInfoList() {
        return data;
    }

    public String getNextUrl() {
        if (pagination != null) {
            return pagination.next_url;
        }
        return null;
    }

    static class Pagination {
        @SerializedName(value = "next_max_tag_id")
        String next_max_tag_id;
        @SerializedName(value = "min_tag_id")
        String min_tag_id;
        @SerializedName(value = "next_url")
        String next_url;
    }

    static class Meta {
        @SerializedName(value = "code")
        int code;
    }

    static class ItemData implements PhotoInfo {
        @SerializedName(value = "tags")
        List<String> tags;
        @SerializedName(value = "location")
        Location location;
        @SerializedName(value = "comments")
        Comments comments;
        @SerializedName(value = "filter")
        String filter;
        @SerializedName(value = "created_time")
        int created_time;
        @SerializedName(value = "link")
        String link;
        @SerializedName(value = "likes")
        Likes likes;
        @SerializedName(value = "images")
        Images images;
        @SerializedName(value = "users_in_photo")
        List<User> users_in_photo;
        @SerializedName(value = "caption")
        CommentData caption;
        @SerializedName(value = "type")
        String type;
        @SerializedName(value = "id")
        String id;
        @SerializedName(value = "user")
        User user;

        @Override
        public String getImageUrl() {
            if (images != null && images.standard_resolution != null) {
                return images.standard_resolution.url;
            }
            return null;
        }

        @Override
        public String getThumbnailUrl() {
            if (images != null && images.thumbnail != null) {
                return images.thumbnail.url;
            }
            return null;
        }

        @Override
        public String getTitle() {
            if (caption != null) {
                return caption.text;
            }
            return null;
        }

        @Override
        public String getShareSubject() {
            String shareSubject;
            shareSubject = "[ImageSearchWithVolley] " +
                    ((!Strings.isNullOrEmpty(getTitle()) && getTitle().length() > 60) ? getTitle().substring(0, 60) : getTitle());
            return shareSubject;
        }

        private String getPhotoPageUrl() {
            return link;
        }

        @Override
        public String getShareText() {
            String shareText = "Instagram";
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


        static class Location {
            @SerializedName(value = "latitude")
            String latitude;
            @SerializedName(value = "longitude")
            String longitutde;
            @SerializedName(value = "name")
            String name;
            @SerializedName(value = "id")
            int id;
        }

        static class Comments {
            @SerializedName(value = "count")
            int count;
            @SerializedName(value = "data")
            List<CommentData> data;
        }

        static class Likes {
            @SerializedName(value = "count")
            int count;
            @SerializedName(value = "data")
            List<User> data;
        }

        static class Images {
            @SerializedName(value = "low_resolution")
            Image low_resolution;
            @SerializedName(value = "thumbnail")
            Image thumbnail;
            @SerializedName(value = "standard_resolution")
            Image standard_resolution;

        }

        static class CommentData {
            @SerializedName(value = "created_time")
            int created_time;
            @SerializedName(value = "text")
            String text;
            @SerializedName(value = "from")
            User from;
            @SerializedName(value = "id")
            String id;

        }

        static class User {
            @SerializedName(value = "username")
            String username;
            @SerializedName(value = "website")
            String website;
            @SerializedName(value = "profile_picture")
            String profile_picture;
            @SerializedName(value = "full_name")
            String full_name;
            @SerializedName(value = "bio")
            String bio;
            @SerializedName(value = "id")
            String id;
        }

        static class Image {
            @SerializedName(value = "url")
            String url;
            @SerializedName(value = "width")
            int width;
            @SerializedName(value = "height")
            int height;
        }
    }
}
