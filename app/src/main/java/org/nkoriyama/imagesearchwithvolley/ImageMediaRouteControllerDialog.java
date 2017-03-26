package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.MediaRouteControllerDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.common.base.Strings;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ImageMediaRouteControllerDialog extends MediaRouteControllerDialog {
    final private GoogleCastManager mCastManager;
    @BindView(R.id.controller_icon)
    NetworkImageView mIcon;
    @BindView(R.id.controller_title)
    TextView mTitle;
    @BindView(R.id.controller_subtitle)
    TextView mSubtitle;

    public ImageMediaRouteControllerDialog(Context context) {
        super(context);

        mCastManager = GoogleCastManager.getInstance();
    }

    @Override
    public View onCreateMediaControlView(Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_imagemediaroutecontroller, null);
        ButterKnife.bind(this, view);

        updateMetadata();

        return view;
    }

    private void updateMetadata() {
        MediaInfo info = mCastManager.getRemoteMediaInformation();
        if (info == null) {
            return;
        }
        MediaMetadata metadata = info.getMetadata();
        String title = metadata.getString(MediaMetadata.KEY_TITLE);
        String subtitle = metadata.getString(MediaMetadata.KEY_SUBTITLE);
        String iconUrl = metadata.hasImages() ? metadata.getImages().get(0).getUrl().toString() : "";

        if (!Strings.isNullOrEmpty(title)) {
            mTitle.setText(title);
        }
        if (!Strings.isNullOrEmpty(subtitle)) {
            mSubtitle.setText(subtitle);
        }
        if (!Strings.isNullOrEmpty(iconUrl)) {
            mIcon.setImageUrl(iconUrl, ImageLoaderHelper.getImageLoader());
        }
    }
}
