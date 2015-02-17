package de.dreier.mytargets.managers;

import android.content.Context;
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

import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.models.OnTargetSetListener;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.WearableConst;

public class WearMessageManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String TAG = "wearMessageManager";
    private final OnTargetSetListener mListener;
    private final Round round;
    private final boolean mode;
    private Bow bow;

    private GoogleApiClient mGoogleApiClient;

    public WearMessageManager(Context context, Round round, boolean mode, Bow bow) {
        this.round = round;
        this.mode = mode;
        this.bow = bow;
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
        sendMessage(WearableConst.STARTED_ROUND, round, mode, bow);
    }

    public Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        if (nodes != null) {
            for (Node node : nodes.getNodes()) {
                results.add(node.getId());
            }
        }
        return results;
    }


    private void sendMessage(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(path, new byte[0]);
            }
        }).start();
    }

    public void sendMessage(final String path, Round r, boolean mode, Bow bow) {
        // Serialize bundle to byte array
        byte[] data = new byte[0];
        try {
            data = WearableConst.serialize(r, mode, bow);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] finalData = data;
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(path, finalData);
            }
        }).start();
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
        Passe p = null;
        try {
            p = WearableConst.deserializeToPasse(data);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (messageEvent.getPath().equals(WearableConst.FINISHED_INPUT)) {
            mListener.onTargetSet(p);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void close() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            sendMessage(WearableConst.STOPPED_ROUND);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
}
