/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.parceler.Parcels;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.shared.models.db.Passe;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.utils.WearableUtils;
import de.dreier.mytargets.shared.views.TargetViewBase;

public class MainActivity extends Activity implements TargetViewBase.OnEndFinishedListener,
        GoogleApiClient.ConnectionCallbacks, WatchViewStub.OnLayoutInflatedListener {

    public static final String EXTRA_ROUND = "round";
    private TargetSelectView mTarget;
    private DelayedConfirmationView confirm;
    private Round round;
    private GoogleApiClient mGoogleApiClient;
    private WatchViewStub stub;
    private TextView startTrainingHint;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            round = Parcels.unwrap(intent.getExtras().getParcelable(EXTRA_ROUND));
            stub.setOnLayoutInflatedListener(MainActivity.this);
            setUpTargetView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            round = Parcels.unwrap(intent.getExtras().getParcelable(EXTRA_ROUND));
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();

        startTrainingHint = (TextView) findViewById(R.id.start_training_hint);
        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(this);

        final IntentFilter intentFilter = new IntentFilter(WearableListener.TRAINING_STARTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onLayoutInflated(WatchViewStub stub1) {
        mTarget = (TargetSelectView) stub1.findViewById(R.id.target);
        confirm = (DelayedConfirmationView) stub1.findViewById(R.id.delayed_confirm);

        // Workaround to avoid crash happening when setting invisible via xml layout
        confirm.setVisibility(View.INVISIBLE);

        // Set up target view
        setUpTargetView();

        // Ensure Moto 360 is not cut off at the bottom
        stub1.setOnApplyWindowInsetsListener((v, insets) -> {
            int chinHeight = insets.getSystemWindowInsetBottom();
            mTarget.setChinHeight(chinHeight);
            return insets;
        });
    }

    private void setUpTargetView() {
        if (round != null && mTarget != null) {
            mTarget.setTarget(round.info.target);
            mTarget.setEnd(new Passe(round.info.arrowsPerEnd));
            mTarget.setOnTargetSetListener(MainActivity.this);
            stub.setVisibility(View.VISIBLE);
            startTrainingHint.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEndFinished(final List<Shot> shotList, boolean remote) {
        confirm.setVisibility(View.VISIBLE);
        confirm.setTotalTimeMs(2500);
        confirm.start();
        confirm.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerSelected(View view) {
                mTarget.setEnd(new Passe(round.info.arrowsPerEnd));
                confirm.setVisibility(View.INVISIBLE);
                confirm.reset();
            }

            @Override
            public void onTimerFinished(View view) {
                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.saved));
                startActivity(intent);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
                finish();
                sendMessage(shotList);
            }
        });
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private void sendMessage(List<Shot> p) {
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(p));
        new Thread(() -> {
            sendMessage(WearableUtils.FINISHED_INPUT, data);
        }).start();
    }

    private void sendMessage(String path, byte[] data) {
        // Send message to all available nodes
        final Collection<String> nodes = getNodes();
        for (String nodeId : nodes) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, nodeId, path, data).setResultCallback(
                    sendMessageResult -> {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e("", "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
            );
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
