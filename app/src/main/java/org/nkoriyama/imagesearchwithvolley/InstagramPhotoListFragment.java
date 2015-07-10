package org.nkoriyama.imagesearchwithvolley;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;

import org.nkoriyama.imagesearchwithvolley.model.InstagramPhotoResponse;

public class InstagramPhotoListFragment extends PhotoListFragment {
    private final String INSTAGRAM_CLIENT_ID = BuildConfig.INSTAGRAM_CLIENT_ID;

    private String mPhotoListUrl;

    public static PhotoListFragment newInstance(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        PhotoListFragment photoListFragment = new InstagramPhotoListFragment();
        Bundle bundle = new Bundle();
        setBundle(bundle, query, 0, 20);
        photoListFragment.setArguments(bundle);
        return photoListFragment;
    }

    private String getPhotoListUrl() {
        String url;

        if (Strings.isNullOrEmpty(mPhotoListUrl)) {
            url = "https://api.instagram.com/v1/tags/" +
                    UrlEscapers.urlPathSegmentEscaper().escape(mQuery.replaceAll("\\s+", "")) +
                    "/media/recent" +
                    "?client_id=" + INSTAGRAM_CLIENT_ID;
        } else {
            url = mPhotoListUrl;
        }

        return url;
    }

    @Override
    protected void loadMoreItems() {
        mPhotoAdapter.mIsInUse = true;
        RequestQueueHelper.getRequestQueue().add(new GsonRequest<>(
                getPhotoListUrl(),
                InstagramPhotoResponse.class,
                null,
                new Response.Listener<InstagramPhotoResponse>() {
                    @Override
                    public void onResponse(InstagramPhotoResponse instagramPhotoResponse) {
                        if (instagramPhotoResponse != null && instagramPhotoResponse.isOK()) {
                            mPhotoAdapter.addAll(instagramPhotoResponse.getPhotoInfoList());
                            mPhotoListUrl = instagramPhotoResponse.getNextUrl();
                            mHasMoreItems = !Strings.isNullOrEmpty(mPhotoListUrl);
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
        ));
    }
}
