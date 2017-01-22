/*
 * Copyright (C) 2017 Florian Dreier
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
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.parceler.Parcels;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.utils.ParcelableUtil;
import de.dreier.mytargets.shared.utils.WearableUtils;
import de.dreier.mytargets.shared.views.TargetViewBase;

public class InputActivity extends Activity implements TargetViewBase.OnEndFinishedListener,
        GoogleApiClient.ConnectionCallbacks {

    public static final String EXTRA_ROUND = "round";
    private static final String EXTRA_SHOTS = "shots";
    private TargetSelectView targetView;
    private DelayedConfirmationView confirm;
    private Round round;
    private GoogleApiClient googleApiClient;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            round = Parcels.unwrap(intent.getExtras().getParcelable(EXTRA_ROUND));
            setUpTargetView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            round = Parcels.unwrap(intent.getExtras().getParcelable(EXTRA_ROUND));
        } else {
            round = new Round();
            round.setTarget(new Target(0, 0));
            round.shotsPerEnd = 6;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        googleApiClient.connect();

        FrameLayout root = (FrameLayout) findViewById(R.id.rootFrameLayout);
        targetView = (TargetSelectView) findViewById(R.id.target);
        confirm = (DelayedConfirmationView) findViewById(R.id.delayedConfirm);

        // Workaround to avoid crash happening when setting invisible via xml layout
        confirm.setVisibility(View.INVISIBLE);

        // Set up target view
        setUpTargetView();

        // Ensure Moto 360 is not cut off at the bottom
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            int chinHeight = insets.getSystemWindowInsetBottom();
            targetView.setChinHeight(chinHeight);
            return insets;
        });

        final IntentFilter intentFilter = new IntentFilter(WearableListener.TRAINING_STARTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void setUpTargetView() {
        if (round != null && targetView != null) {
            targetView.setTarget(round.getTarget());
            targetView.setEnd(new End(round.shotsPerEnd, 0));
            targetView.setOnTargetSetListener(this);
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
                targetView.setEnd(new End(round.shotsPerEnd, 0));
                confirm.setVisibility(View.INVISIBLE);
                confirm.reset();
            }

            @Override
            public void onTimerFinished(View view) {
                Intent intent = new Intent(InputActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                        ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.saved));
                startActivity(intent);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
                finish();
                Intent i = new Intent();
                i.putExtra(EXTRA_SHOTS, Parcels.wrap(shotList));
                setResult(RESULT_OK, i);
                sendMessage(shotList);
            }
        });
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private void sendMessage(List<Shot> p) {
        final byte[] data = ParcelableUtil.marshall(Parcels.wrap(p));
        new Thread(() -> {
            sendMessage(WearableUtils.END_UPDATE, data);
        }).start();
    }

    private void sendMessage(String path, byte[] data) {
        // Send message to all available nodes
        final Collection<String> nodes = getNodes();
        for (String nodeId : nodes) {
            Wearable.MessageApi.sendMessage(
                    googleApiClient, nodeId, path, data).setResultCallback(
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
