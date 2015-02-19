package de.dreier.mytargets.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import de.dreier.mytargets.models.OnTargetSetListener;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.models.WearableUtils;

public class WearMessageManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String TAG = "wearMessageManager";
    private final OnTargetSetListener mListener;
    private final WearableUtils.NotificationInfo info;
    private final Bitmap image;

    private final GoogleApiClient mGoogleApiClient;

    public WearMessageManager(Context context, Bitmap image, WearableUtils.NotificationInfo info) {
        this.image = image;
        this.info = info;
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        if (!(context instanceof OnTargetSetListener))
            throw new ClassCastException();

        mListener = (OnTargetSetListener) context;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        sendMessage(image, info);
    }

    Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        if (nodes != null) {
            for (Node node : nodes.getNodes()) {
                results.add(node.getId());
            }
        }
        return results;
    }

    void sendMessage(Bitmap image, WearableUtils.NotificationInfo info) {
        // Serialize bundle to byte array
        try {
            final byte[] data = WearableUtils.serialize(image, info);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendMessage(WearableUtils.STARTED_ROUND, data);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(WearableUtils.NotificationInfo info) {
        // Serialize info to byte array
        try {
            final byte[] data = WearableUtils.serialize(info);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendMessage(WearableUtils.UPDATE_ROUND, data);
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String path, byte[] data) {
        // Send message to all available nodes
        final Collection<String> nodes = getNodes();
        for (String nodeId : nodes) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, nodeId, path, data).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // Transform byte[] to Bundle
        byte[] data = messageEvent.getData();
        Shot[] p = null;
        try {
            p = WearableUtils.deserializeToPasse(data);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (messageEvent.getPath().equals(WearableUtils.FINISHED_INPUT)) {
            mListener.onTargetSet(p, true);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void close() {
        if (mGoogleApiClient.isConnected()) {
            sendMessageStopped();
        }
    }

    private void sendMessageStopped() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(WearableUtils.STOPPED_ROUND, new byte[0]);
                Wearable.MessageApi.removeListener(mGoogleApiClient, WearMessageManager.this);
                mGoogleApiClient.disconnect();
            }
        }).start();
    }
}
