package org.nkoriyama.imagesearchwithvolley;


import android.os.Bundle;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;

import org.nkoriyama.imagesearchwithvolley.model.BingPhotoResponse;

import java.util.HashMap;
import java.util.Map;

public class BingPhotoListFragment extends PhotoListFragment {
    private final String BING_API_KEY = BuildConfig.BING_API_KEY;

    public static PhotoListFragment newInstance(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        PhotoListFragment photoListFragment = new BingPhotoListFragment();
        Bundle bundle = new Bundle();
        setBundle(bundle, query, 0, 50);//def35 max150
        photoListFragment.setArguments(bundle);
        return photoListFragment;
    }

    private String getPhotoListUrl() {
        String ret;
        ret = "https://api.cognitive.microsoft.com/bing/v5.0/images/search" +
                "?q=" + UrlEscapers.urlPathSegmentEscaper().escape(mQuery) +
                "&offset=" + (mPage * mPerPage) +
                "&count=" + mPerPage;
        return ret;
    }

    @Override
    protected void loadMoreItems() {
        mPhotoAdapter.mIsInUse = true;
        RequestQueueHelper.getRequestQueue().add(new MoshiRequest<BingPhotoResponse>(
                getPhotoListUrl(),
                BingPhotoResponse.class,
                null,
                new Response.Listener<BingPhotoResponse>() {
                    @Override
                    public void onResponse(BingPhotoResponse bingPhotoResponse) {
                        if (bingPhotoResponse != null && bingPhotoResponse.isOK()) {
                            mPhotoAdapter.addAll(bingPhotoResponse.getPhotoInfoList());
                            mHasMoreItems = bingPhotoResponse.hasNext();
                            mPage++;
                        } else {
                            mHasMoreItems = false;
                        }
                        mPhotoAdapter.mIsInUse = false;
                        refreshing(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mPhotoAdapter.mIsInUse = false;
                        refreshing(false);
                        volleyError.printStackTrace();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                Map<String, String> newHeaders = new HashMap<>();
                newHeaders.putAll(headers);
                newHeaders.put("Ocp-Apim-Subscription-Key", BING_API_KEY);
                return newHeaders;
            }
        });
    }
}

