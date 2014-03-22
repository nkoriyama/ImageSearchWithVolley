package org.nkoriyama.imagesearchwithvolley;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.nkoriyama.imagesearchwithvolley.model.PhotoInfo;
import org.nkoriyama.imagesearchwithvolley.model.PhotoInfoParcelable;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PhotoDetailFragment extends Fragment {
    @InjectView(R.id.detail_title)
    TextView mTitle;
    @InjectView(R.id.detail_image)
    NetworkImageView mImage;

    public static PhotoDetailFragment newInstance(PhotoInfo photoinfo, boolean zoomEnabled)
    {
        PhotoDetailFragment photodetailfragment = new PhotoDetailFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable("photoInfo", new PhotoInfoParcelable(photoinfo));
        bundle.putBoolean("zoomEnabled", zoomEnabled);
        photodetailfragment.setArguments(bundle);
        return photodetailfragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle bundle;
        if (savedInstanceState != null)
        {
            bundle = savedInstanceState;
        } else
        {
            bundle = getArguments();
        }
        if (bundle == null) {
            return null;
        }

        final PhotoInfoParcelable photoInfo = bundle.getParcelable("photoInfo");
        if (photoInfo == null) {
            return  null;
        }
        final int resource;
        if (bundle.getBoolean("zoomEnabled")) {
            resource = R.layout.fragment_photodetailzoom;
        } else {
            resource = R.layout.fragment_photodetail;
        }

        final View view = inflater.inflate(resource, container, false);
        if (view == null) {
            return null;
        }

        ButterKnife.inject(this, view);

        mTitle.setText(photoInfo.getTitle());
        mTitle.requestFocus();
        final MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return null;
        }

        mImage.setImageUrl(
                photoInfo.getImageUrl(),
                ((ImageSearchWithVolley) activity.getApplication()).getImageLoader()
        );

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
