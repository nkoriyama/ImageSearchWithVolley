package org.nkoriyama.imagesearchwithvolley;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

public class GoogleCastManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "GoogleCastManger";
    private static final int WHAT_UI_VISIBLE = 0;
    private static final int WHAT_UI_HIDDEN = 1;
    private static final int UI_VISIBILITY_DELAY_MS = 300;
    private static GoogleCastManager sInstance;
    private final String mApplicationId;
    private final Handler mUiVisibilityHandler;
    final private MediaRouter mMediaRouter;
    final private MediaRouteSelector mMediaRouteSelector;
    final private Cast.Listener mCastClientListener;
    final private android.support.v7.media.MediaRouter.Callback mMediaRouterCallback;
    private Context mContext;
    private int mVisibilityCounter;
    private boolean mUiVisible;
    private GoogleApiClient mApiClient;
    private CastDevice mSelectedDevice;
    private boolean mWaitingForReconnect;
    private String mSessionId;
    private RemoteMediaPlayer mRemoteMediaPlayer;

    public GoogleCastManager(Context context, String applicationId) {
        mContext = context;
        mApplicationId = applicationId;

        mUiVisibilityHandler = new Handler(new UpdateUiVisibilityHandlerCallback());

        mMediaRouter = MediaRouter.getInstance(mContext);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(mApplicationId))
                .build();
        mMediaRouterCallback = new MediaRouterCallback();

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
                disconnectDevice();
            }
        };

    }

    public static GoogleCastManager getInstance() {
        if (sInstance == null) {
            Log.e(TAG, "No GoogleCastManager instance.");
        }
        return sInstance;
    }

    public static synchronized GoogleCastManager initialize(Context context, String applicationId) {
        if (sInstance == null) {
            sInstance = new GoogleCastManager(context, applicationId);
        }
        return sInstance;
    }

    private void setDevice(CastDevice device) {
        mSelectedDevice = device;
        if (mApiClient == null) {
            Cast.CastOptions.Builder apiOptionsBuilder = new Cast.CastOptions.Builder(mSelectedDevice, mCastClientListener);
            mApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mApiClient.connect();
        } else if (!isConnected() && !isConnecting()) {
            mApiClient.connect();
        }
    }


    private void reconnectDevice(CastDevice device) {
        Log.d(TAG, "reconnectDevice: " + device.getFriendlyName());
        //setDevice(device);
    }

    private void disconnectDevice() {
        Log.d(TAG, "disconnectDevice");
        mSelectedDevice = null;

        mWaitingForReconnect = false;

        if (isConnected() || isConnecting()) {
            stopApplication();
        }

        detachMediaChannel();

        if (mApiClient != null) {
            mApiClient.disconnect();
            if (mMediaRouter != null) {
                mMediaRouter.selectRoute(mMediaRouter.getDefaultRoute());
            }
            mApiClient = null;
        }
        mSessionId = null;
    }

    private void attachMediaChannel() {
        Log.d(TAG, "attachMediaChannel");

        if (mRemoteMediaPlayer == null) {
            mRemoteMediaPlayer = new RemoteMediaPlayer();
        }
        try {
            Cast.CastApi.setMessageReceivedCallbacks(mApiClient, mRemoteMediaPlayer.getNamespace(), mRemoteMediaPlayer);
        } catch (IOException e) {
            Log.e(TAG, "Failed setMessageReceivedCallbacks", e);
        }
    }

    private void detachMediaChannel() {
        Log.d(TAG, "detachMediaChannel");
        if (mRemoteMediaPlayer != null) {
            try {
                Cast.CastApi.removeMessageReceivedCallbacks(mApiClient, mRemoteMediaPlayer.getNamespace());
            } catch (IOException e) {
                Log.e(TAG, "Failed removeMessageReceivedCallbacks", e);
            }
            mRemoteMediaPlayer = null;
        }
    }

    private void onApplicationConnected(final ApplicationMetadata appMetadata, final String applicationStatus,
                                        final String sessionId, final boolean wasLaunched) {
        Log.d(TAG, "onApplicationConnected");

        attachMediaChannel();
        mSessionId = sessionId;
        mRemoteMediaPlayer.requestStatus(mApiClient)
                .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                    @Override
                    public void onResult(RemoteMediaPlayer.MediaChannelResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Log.d(TAG, "requestStatus error: "
                                            + " appMetaData[" + appMetadata.getName() + "]"
                                            + " applicationStatus[" + applicationStatus + "]"
                                            + " sessionId [" + sessionId + "]"
                                            + " wasLaunched [" + wasLaunched + "]"
                            );
                        }
                    }
                });
    }

    private void launchApplication() {
        Log.d(TAG, "Launching application");
        Cast.CastApi.launchApplication(mApiClient, mApplicationId).setResultCallback(
                new ResultCallback<Cast.ApplicationConnectionResult>() {
                    @Override
                    public void onResult(Cast.ApplicationConnectionResult result) {
                        if (result.getStatus().isSuccess()) {
                            Log.d(TAG, "launchApplication() -> success result");
                            onApplicationConnected(
                                    result.getApplicationMetadata(),
                                    result.getApplicationStatus(),
                                    result.getSessionId(),
                                    result.getWasLaunched());
                        } else {
                            Log.d(TAG, "launchApplication() -> failure result");
                        }
                    }
                }
        );
    }

    private void stopApplication() {
        Log.d(TAG, "Stopping application");
        Cast.CastApi.stopApplication(mApiClient, mSessionId)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status result) {
                        if (!result.isSuccess()) {
                            Log.d(TAG, "stopApplication -> onResult: stopping "
                                    + "application failed");
                        } else {
                            Log.d(TAG, "stopApplication -> onResult Stopped application "
                                    + "successfully");
                        }
                    }
                });
    }

    public MenuItem addMediaRouterButton(Menu menu, int menuResourceId) {
        MenuItem mediaRouteMenuItem = menu.findItem(menuResourceId);
        MediaRouteActionProvider mediaRouteActionProvider = (MediaRouteActionProvider)
                MenuItemCompat.getActionProvider(mediaRouteMenuItem);
        mediaRouteActionProvider.setRouteSelector(mMediaRouteSelector);
        mediaRouteActionProvider.setDialogFactory(new ImageMediaRouteDialogFactory());

        return mediaRouteMenuItem;
    }

    public boolean isConnected() {
        return mApiClient != null && mApiClient.isConnected();
    }

    public boolean isConnecting() {
        return mApiClient != null && mApiClient.isConnecting();
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

    public synchronized void incrementUiCounter() {
        mVisibilityCounter++;
        if (!mUiVisible) {
            mUiVisible = true;
            mUiVisibilityHandler.removeMessages(WHAT_UI_HIDDEN);
            mUiVisibilityHandler.sendEmptyMessageDelayed(WHAT_UI_VISIBLE, UI_VISIBILITY_DELAY_MS);
        }
        if (mVisibilityCounter == 0) {
            Log.d(TAG, "UI is no longer visible");
        } else {
            Log.d(TAG, "UI is visible");
        }
    }

    public synchronized void decrementUiCounter() {
        if (--mVisibilityCounter == 0) {
            Log.d(TAG, "UI is no longer visible");
            if (mUiVisible) {
                mUiVisible = false;
                mUiVisibilityHandler.removeMessages(WHAT_UI_VISIBLE);
                mUiVisibilityHandler.sendEmptyMessageDelayed(WHAT_UI_HIDDEN,
                        UI_VISIBILITY_DELAY_MS);
            }
        } else {
            Log.d(TAG, "UI is visible");
        }
    }

    private void onUiVisibilityChanged(final boolean visible) {
        if (visible) {
            if (null != mMediaRouter) {
                Log.d(TAG, "onUiVisibilityChanged() addCallback called");
                startCastDiscovery();
            }
        } else {
            if (null != mMediaRouter) {
                Log.d(TAG, "onUiVisibilityChanged() removeCallback called");
                stopCastDiscovery();
            }
        }
    }

    public void startCastDiscovery() {
        mMediaRouter.addCallback(mMediaRouteSelector, mMediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    public void stopCastDiscovery() {
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    public MediaInfo getRemoteMediaInformation() {
        return mRemoteMediaPlayer.getMediaInfo();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mWaitingForReconnect) {
            mWaitingForReconnect = false;

            if (connectionHint != null && connectionHint.getBoolean(Cast.EXTRA_APP_NO_LONGER_RUNNING)) {
                Log.d(TAG, "onConnected: app no longer running");
                disconnectDevice();
            } else {
                reconnectDevice(mSelectedDevice);
            }
            return;
        }
        if (!isConnected()) {
            return;
        }
        try {
            Cast.CastApi.requestStatus(mApiClient);
            launchApplication();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        mWaitingForReconnect = true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        disconnectDevice();
    }

    public void clearContext(Context context) {
        if (mContext != null && mContext == context) {
            mContext = null;
        }
    }

    private class MediaRouterCallback extends MediaRouter.Callback {
        @Override
        public void onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
            setDevice(CastDevice.getFromBundle(info.getExtras()));
        }

        @Override
        public void onRouteUnselected(MediaRouter router, MediaRouter.RouteInfo info) {
            disconnectDevice();
        }
    }

    private class UpdateUiVisibilityHandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            onUiVisibilityChanged(msg.what == WHAT_UI_VISIBLE);
            return true;
        }
    }
}
