package org.nkoriyama.imagesearchwithvolley;

import android.os.Bundle;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.net.UrlEscapers;

import org.nkoriyama.imagesearchwithvolley.model.FlickrPhotoResponse;

public class FlickrPhotoListFragment extends PhotoListFragment {
    private final String FLICKR_API_KEY = BuildConfig.FLICKR_API_KEY;

    public static PhotoListFragment newInstance(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        PhotoListFragment photoListFragment = new FlickrPhotoListFragment();
        Bundle bundle = new Bundle();
        setBundle(bundle, query, 1, 50);
        photoListFragment.setArguments(bundle);
        return photoListFragment;
    }

    private String getPhotoListUrl() {
        return "https://api.flickr.com/services/rest/" +
                "?method=flickr.photos.search" +
                "&api_key=" + FLICKR_API_KEY +
                "&text=" + UrlEscapers.urlPathSegmentEscaper().escape(mQuery) +
                "&tags=" + UrlEscapers.urlPathSegmentEscaper().escape(mQuery) +
                "&per_page=" + mPerPage +
                "&page=" + mPage +
                "&format=json" +
                "&nojsoncallback=1";
    }

    @Override
    protected void loadMoreItems() {
        mPhotoAdapter.mIsInUse = true;
        RequestQueueHelper.getRequestQueue().add(new GsonRequest<FlickrPhotoResponse>(
                getPhotoListUrl(),
                FlickrPhotoResponse.class,
                null,
                new Response.Listener<FlickrPhotoResponse>() {
                    @Override
                    public void onResponse(FlickrPhotoResponse flickrPhotoResponse) {
                        if (flickrPhotoResponse != null && flickrPhotoResponse.isOK()) {
                            mPhotoAdapter.addAll(flickrPhotoResponse.getPhotoInfoList());
                            mHasMoreItems = (mPage * mPerPage) < flickrPhotoResponse.getTotal();
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
