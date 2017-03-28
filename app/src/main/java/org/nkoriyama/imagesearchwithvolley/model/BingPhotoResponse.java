package org.nkoriyama.imagesearchwithvolley.model;

import com.google.common.base.Strings;
import com.squareup.moshi.Json;

import java.util.List;

public class BingPhotoResponse {
    public boolean isOK() {
        return (value != null);
    }
    public boolean hasNext() {
        return (nextOffsetAddCount > 0);
    }

    public List<Image> getPhotoInfoList() {
        return value;
    }

    String _type;
    boolean displayRecipeSourcesBadges;
    boolean displayShoppingSourcesBadges;
    Instrumentation instrumentation;
    List<Pivot> pivotSuggestions;
    List<Query> queryExpansions;
    String readLink;
    Long totalEstimatedMatches;
    String id;
    boolean isFamilyFriendly;
    int nextOffsetAddCount;
    List<Image> value;
    String webSearchUrl;
    String webSearchUrlPingSuffix;

    static class Instrumentation {
        String pageLoadPingUrl;
        String pingUrlBase;
    }

    static class Pivot {
        String pivot;
    }
    static class Query {
        String q;
    }
    static class Image implements PhotoInfo {
        String accentColor;
        String contentSize;
        String contentUrl;
        String datePublished;
        String encodingFormat;
        short height;
        String hostPageDisplayUrl;
        String hostPageUrl;
        String hostPageUrlPingSuffix;
        String id;
        String imageId;
        String imageInsightsToken;
        InsightsSourcesSummary insightsSourcesSummary;
        String name;
        MediaSize thumbnail;
        String thumbnailUrl;
        String webSearchUrl;
        String webSearchUrlPingSuffix;
        int width;

        @Override
        public String getImageUrl() {
            return contentUrl;
        }

        @Override
        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        @Override
        public String getTitle() {
            return name;
        }

        @Override
        public String getShareSubject() {
            String shareSubject;
            shareSubject = "[ImageSearchWithVolley] " +
                    ((!Strings.isNullOrEmpty(name) && name.length() > 60) ? name.substring(0, 60) : name);
            return shareSubject;        }

        @Override
        public String getShareText() {
            String shareText = "Bing";
            shareText += System.getProperty("line.separator") + "Title:[" + name + "]";
            shareText += System.getProperty("line.separator") + "Image URL:[" + getImageUrl() + "]";
            shareText += System.getProperty("line.separator") + "Page URL:[" + hostPageDisplayUrl + "]";
            return shareText;
        }
    }
    static class InsightsSourcesSummary {
        int recipeSourcesCount;
        int shoppingSourcesCount;
    }
    static class MediaSize {
        int height;
        int width;
    }
}
