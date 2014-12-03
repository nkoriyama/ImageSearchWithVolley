package org.nkoriyama.imagesearchwithvolley;

import android.support.annotation.NonNull;
import android.support.v7.app.MediaRouteDialogFactory;

public class ImageMediaRouteDialogFactory extends MediaRouteDialogFactory {

    @NonNull
    @Override
    public ImageMediaRouteControllerDialogFragment onCreateControllerDialogFragment() {
        return new ImageMediaRouteControllerDialogFragment();
    }
}
