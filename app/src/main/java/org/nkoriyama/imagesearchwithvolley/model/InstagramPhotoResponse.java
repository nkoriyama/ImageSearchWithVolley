package org.nkoriyama.imagesearchwithvolley.model;

import com.google.common.base.Strings;
import com.squareup.moshi.Json;

import java.util.List;

public class InstagramPhotoResponse {
    @Json(name = "pagination")
    Pagination pagination;
    @Json(name = "meta")
    Meta meta;
    @Json(name = "data")
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
        @Json(name = "next_max_tag_id")
        String next_max_tag_id;
        @Json(name = "min_tag_id")
        String min_tag_id;
        @Json(name = "next_url")
        String next_url;
    }

    static class Meta {
        @Json(name = "code")
        int code;
    }

    static class ItemData implements PhotoInfo {
        @Json(name = "tags")
        List<String> tags;
        @Json(name = "location")
        Location location;
        @Json(name = "comments")
        Comments comments;
        @Json(name = "filter")
        String filter;
        @Json(name = "created_time")
        int created_time;
        @Json(name = "link")
        String link;
        @Json(name = "likes")
        Likes likes;
        @Json(name = "images")
        Images images;
        @Json(name = "users_in_photo")
        List<User> users_in_photo;
        @Json(name = "caption")
        CommentData caption;
        @Json(name = "type")
        String type;
        @Json(name = "id")
        String id;
        @Json(name = "user")
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
            @Json(name = "latitude")
            String latitude;
            @Json(name = "longitude")
            String longitutde;
            @Json(name = "name")
            String name;
            @Json(name = "id")
            int id;
        }

        static class Comments {
            @Json(name = "count")
            int count;
            @Json(name = "data")
            List<CommentData> data;
        }

        static class Likes {
            @Json(name = "count")
            int count;
            @Json(name = "data")
            List<User> data;
        }

        static class Images {
            @Json(name = "low_resolution")
            Image low_resolution;
            @Json(name = "thumbnail")
            Image thumbnail;
            @Json(name = "standard_resolution")
            Image standard_resolution;

        }

        static class CommentData {
            @Json(name = "created_time")
            int created_time;
            @Json(name = "text")
            String text;
            @Json(name = "from")
            User from;
            @Json(name = "id")
            String id;

        }

        static class User {
            @Json(name = "username")
            String username;
            @Json(name = "website")
            String website;
            @Json(name = "profile_picture")
            String profile_picture;
            @Json(name = "full_name")
            String full_name;
            @Json(name = "bio")
            String bio;
            @Json(name = "id")
            String id;
        }

        static class Image {
            @Json(name = "url")
            String url;
            @Json(name = "width")
            int width;
            @Json(name = "height")
            int height;
        }
    }
}
