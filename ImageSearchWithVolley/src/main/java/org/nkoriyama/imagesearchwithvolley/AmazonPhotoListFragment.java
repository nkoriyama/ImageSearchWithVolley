package org.nkoriyama.imagesearchwithvolley;

import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import org.nkoriyama.imagesearchwithvolley.model.AmazonPhotoResponse;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AmazonPhotoListFragment extends PhotoListFragment {
    private final String AWS_ENDPOINT = BuildConfig.AWS_ENDPOINT;
    private final String AWS_ACCESS_KEY_ID = BuildConfig.AWS_ACCESS_KEY_ID;
    private final String AWS_SECRET_KEY = BuildConfig.AWS_SECRET_KEY;
    private final String AWS_ASSOCIATE_TAG = BuildConfig.AWS_ASSOCIATE_TAG;
    private SignedRequestsHelper helper;

    public static PhotoListFragment newInstance(String query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query));
        PhotoListFragment photoListFragment = new AmazonPhotoListFragment();
        Bundle bundle = new Bundle();
        setBundle(bundle, query, 1, 10);
        photoListFragment.setArguments(bundle);
        return photoListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            helper = SignedRequestsHelper.getInstance(AWS_ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getPhotoListUrl() {
        return helper.sign(
                "Service=AWSECommerceService" +
                        "&Version=2011-08-01" +
                        "&AssociateTag=" + AWS_ASSOCIATE_TAG +
                        "&Operation=ItemSearch" +
                        "&SearchIndex=Blended" +
                        "&ResponseGroup=ItemAttributes,Images,Offers" +
                        "&Keywords=" + mQuery +
                        "&ItemPage=" + mPage++);
    }

    @Override
    protected void loadMoreItems() {
        mIsLoading = true;
        mRequestQueue.add(new SimpleXmlRequest<AmazonPhotoResponse>(
                Request.Method.GET,
                getPhotoListUrl(),
                AmazonPhotoResponse.class,
                new Response.Listener<AmazonPhotoResponse>() {
                    @Override
                    public void onResponse(AmazonPhotoResponse amazonPhotoResponse) {
                        if (amazonPhotoResponse != null && amazonPhotoResponse.isOK()) {
                            mPhotoAdapter.addAll(amazonPhotoResponse.getPhotoInfoList());
                            mTotal = 10 * mPerPage; // 最大10ページまで
                        }
                        mIsLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        --mPage;
                        mIsLoading = false;
                        volleyError.printStackTrace();
                    }
                }
        ));
    }
}
