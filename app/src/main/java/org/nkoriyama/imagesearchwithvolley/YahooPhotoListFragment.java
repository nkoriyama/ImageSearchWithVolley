package org.nkoriyama.imagesearchwithvolley;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;

import org.nkoriyama.imagesearchwithvolley.model.YahooPhotoResponse;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

public class YahooPhotoListFragment extends PhotoListFragment {
    private final String YAHOO_API_CONSUMER_KEY = BuildConfig.YAHOO_API_CONSUMER_KEY;
    private final String YAHOO_API_CONSUMER_SECRET = BuildConfig.YAHOO_API_CONSUMER_SECRET;

    private final OAuthConsumer mConsumer = new DefaultOAuthConsumer(
            YAHOO_API_CONSUMER_KEY,
            YAHOO_API_CONSUMER_SECRET
    );

    public static PhotoListFragment newInstance(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        PhotoListFragment photoListFragment = new YahooPhotoListFragment();
        Bundle bundle = new Bundle();
        setBundle(bundle, query, 0, 35);
        photoListFragment.setArguments(bundle);
        return photoListFragment;
    }

    private String getPhotoListUrl() {
        return "https://yboss.yahooapis.com/ysearch/images" +
                "?q=" + UrlEscapers.urlPathSegmentEscaper().escape("\"" + mQuery + "\"") +
                "&format=json" +
                "&start=" + mPage * mPerPage +
                "&count=" + mPerPage;
    }

    @Override
    protected void loadMoreItems() {
        mPhotoAdapter.mIsInUse = true;
        RequestQueueHelper.getRequestQueue(mConsumer).add(new GsonRequest<YahooPhotoResponse>(
                getPhotoListUrl(),
                YahooPhotoResponse.class,
                null,
                new Response.Listener<YahooPhotoResponse>() {
                    @Override
                    public void onResponse(YahooPhotoResponse yahooPhotoResponse) {
                        if (yahooPhotoResponse != null && yahooPhotoResponse.isOK()) {
                            mPhotoAdapter.addAll(yahooPhotoResponse.getPhotoInfoList());
                            mHasMoreItems = (mPage * mPerPage) < yahooPhotoResponse.getTotal();
                            mPage++;
                        } else {
                            mHasMoreItems = false;
                        }
                        mPhotoAdapter.mIsInUse = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mPhotoAdapter.mIsInUse = false;
                        volleyError.printStackTrace();
                    }
                }
        ));
    }
}
