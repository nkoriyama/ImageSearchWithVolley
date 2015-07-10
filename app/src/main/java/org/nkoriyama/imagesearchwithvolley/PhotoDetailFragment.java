package org.nkoriyama.imagesearchwithvolley;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.common.base.Preconditions;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;
import org.nkoriyama.imagesearchwithvolley.model.PhotoInfoParcelable;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PhotoDetailFragment extends Fragment {
    @Bind(R.id.detail_title)
    TextView mTitle;
    @Bind(R.id.detail_image)
    NetworkImageView mImage;

    private PhotoInfo mPhotoInfo;
    private boolean mZoomEnabled;

    public static PhotoDetailFragment newInstance(PhotoInfo photoInfo, boolean zoomEnabled) {
        Preconditions.checkNotNull(photoInfo);
        PhotoDetailFragment photodetailfragment = new PhotoDetailFragment();
        final Bundle bundle = new Bundle();
        setBundle(bundle, photoInfo, zoomEnabled);
        photodetailfragment.setArguments(bundle);
        return photodetailfragment;
    }

    private static void setBundle(Bundle bundle, PhotoInfo photoInfo, boolean zoomEnabled) {
        Preconditions.checkNotNull(bundle);
        Preconditions.checkNotNull(photoInfo);
        bundle.putParcelable("photoInfo", new PhotoInfoParcelable(photoInfo));
        bundle.putBoolean("zoomEnabled", zoomEnabled);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle bundle;
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
        } else {
            bundle = getArguments();
        }
        assert bundle != null;

        mPhotoInfo = bundle.getParcelable("photoInfo");
        assert mPhotoInfo != null;
        mZoomEnabled = bundle.getBoolean("zoomEnabled");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int resource;
        if (mZoomEnabled) {
            resource = R.layout.fragment_photodetailzoom;
        } else {
            resource = R.layout.fragment_photodetail;
        }

        final View view = inflater.inflate(resource, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTitle.setText(mPhotoInfo.getTitle());
        mTitle.requestFocus();

        mImage.setImageUrl(
                mPhotoInfo.getImageUrl(),
                ImageLoaderHelper.getImageLoader()
        );

        final MainActivity activity = (MainActivity) getActivity();
        assert activity != null;

        if (mZoomEnabled) {
            activity.updateActionBar(mPhotoInfo);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setBundle(outState, mPhotoInfo, mZoomEnabled);
    }
}
