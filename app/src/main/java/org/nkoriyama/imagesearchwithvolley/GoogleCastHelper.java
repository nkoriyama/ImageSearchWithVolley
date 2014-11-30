package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class GoogleCastHelper {
    private final String TAG = "org.nkoriyama.imagesearchwithvolley.GoogleCastHelper";

    private Context mContext;
    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private GoogleApiClient mApiClient;
    private CastDevice mSelectedDevice;
    private Cast.Listener mCastClientListener;
    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks;
    private boolean mWaitingForReconnect;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private boolean mApplicationStarted;
    private String mSessionId;
    private RemoteMediaPlayer mRemoteMediaPlayer;
    private android.support.v7.media.MediaRouter.Callback mMediaRouterCallback;

    public GoogleCastHelper(Context context) {
        mContext = context;

        mMediaRouter = MediaRouter.getInstance(mContext);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
                .build();
        mConnectionFailedListener = new ConnectionFailedListener();
        mCastClientListener = new Cast.Listener() {
            @Override
            public void onApplicationStatusChanged() {
                super.onApplicationStatusChanged();
                Log.d(TAG, "onApplicationStatusChanged: "
                        + Cast.CastApi.getApplicationStatus(mApiClient));
            }

            @Override
            public void onVolumeChanged() {
                super.onVolumeChanged();
            }

            @Override
            public void onApplicationDisconnected(int statusCode) {
                super.onApplicationDisconnected(statusCode);
                Log.d(TAG, "onApplicationDisconnected: " + statusCode);
                teardown();
            }
        };
        mConnectionCallbacks = new ConnectionCallbacks();
        mRemoteMediaPlayer = new RemoteMediaPlayer();
        mMediaRouterCallback = new MyMediaRouterCallback();
    }

    private void connect(CastDevice device) {
        mSelectedDevice = device;
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder(mSelectedDevice, mCastClientListener);
        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Cast.API, apiOptionsBuilder.build())
                .addConnectionCallbacks(mConnectionCallbacks)
                .addOnConnectionFailedListener(mConnectionFailedListener)
                .build();
        mApiClient.connect();
    }

    private void reconnectChannels(CastDevice device) {
        connect(device);
    }

    private void teardown() {
        Log.d(TAG, "teardown");
        if (mApiClient != null) {
            if (mApplicationStarted) {
                if (mApiClient.isConnected() || mApiClient.isConnecting()) {
                    Cast.CastApi.stopApplication(mApiClient, mSessionId);
                    mApiClient.disconnect();
                }
                mApplicationStarted = false;
            }
            mApiClient = null;
        }
        mSelectedDevice = null;
        mWaitingForReconnect = false;
        mSessionId = null;
    }

    public MenuItem addMediaRouterButton(Menu menu, int menuResourceId) {
        MenuItem mediaRouteMenuItem = menu.findItem(menuResourceId);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider)
                MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);

        return mediaRouteMenuItem;
    }

    public boolean canCast() {
        return mApiClient != null && mApiClient.isConnected() && mApplicationStarted;
    }

    public void loadMedia(MediaInfo mediaInfo) {
        try {
            mRemoteMediaPlayer.load(mApiClient, mediaInfo, true)
                    .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult(RemoteMediaPlayer.MediaChannelResult result) {
                            if (result.getStatus().isSuccess()) {
                                Log.d(TAG, "Media loaded successfully");
                            }
                        }
                    });
        } catch (IllegalStateException e) {
            Log.e(TAG, "Problem occurred with media during loading", e);
        } catch (Exception e) {
            Log.e(TAG, "Problem opening media during loading", e);
        }
    }

    public void addCallback() {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void removeCallback() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    private class ConnectionCallbacks implements
            GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(Bundle connectionHint) {
            if (mWaitingForReconnect) {
                mWaitingForReconnect = false;
                reconnectChannels(mSelectedDevice);
            } else {
                try {
                    Cast.CastApi.launchApplication(mApiClient, CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID, false)
                            .setResultCallback(
                                    new ResultCallback<Cast.ApplicationConnectionResult>() {
                                        @Override
                                        public void onResult(Cast.ApplicationConnectionResult result) {
                                            Status status = result.getStatus();
                                            if (status.isSuccess()) {
                                                //ApplicationMetadata applicationMetadata = result.getApplicationMetadata();
                                                String sessionId = result.getSessionId();
                                                //String applicationStatus = result.getApplicationStatus();
                                                //boolean wasLaunched = result.getWasLaunched();

                                                mApplicationStarted = true;
                                                mSessionId = sessionId;
                                            } else {
                                                teardown();
                                            }
                                        }
                                    });
                } catch (Exception e) {
                    Log.e(TAG, "Failed to launch application", e);
                }
            }
        }

        @Override
        public void onConnectionSuspended(int cause) {
            mWaitingForReconnect = true;
        }
    }

    private class MyMediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            connect(CastDevice.getFromBundle(info.getExtras()));
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            teardown();
        }
    }

    private class ConnectionFailedListener implements
            GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            teardown();
        }
    }
}
