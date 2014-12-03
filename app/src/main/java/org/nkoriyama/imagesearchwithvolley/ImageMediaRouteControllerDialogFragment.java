package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.MediaRouteControllerDialogFragment;

public class ImageMediaRouteControllerDialogFragment extends MediaRouteControllerDialogFragment {

    private ImageMediaRouteControllerDialog mCustomControllerDialog;

    @Override
    public ImageMediaRouteControllerDialog onCreateControllerDialog(Context context, Bundle savedInstanceState) {
        mCustomControllerDialog = new ImageMediaRouteControllerDialog(context);
        mCustomControllerDialog.setVolumeControlEnabled(false);

        return mCustomControllerDialog;
    }
}
