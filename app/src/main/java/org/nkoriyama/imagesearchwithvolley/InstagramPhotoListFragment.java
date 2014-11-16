package org.nkoriyama.imagesearchwithvolley;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.nkoriyama.imagesearchwithvolley.model.InstagramPhotoResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        String url = "";

        if (Strings.isNullOrEmpty(mPhotoListUrl)) {
            try {
                url = "https://api.instagram.com/v1/tags/" +
                        URLEncoder.encode(mQuery.replaceAll("\\s+", ""), "utf-8") +
                        "/media/recent?client_id=" + INSTAGRAM_CLIENT_ID;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            url = mPhotoListUrl;
        }

        return url;
    }

    @Override
    protected void loadMoreItems() {
        mPhotoAdapter.mIsInUse = true;
        RequestQueueHelper.getRequestQueue().add(new GsonRequest<InstagramPhotoResponse>(
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
