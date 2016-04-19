package de.dreier.mytargets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.utils.OnTargetSetListener;
import de.dreier.mytargets.shared.utils.WearableUtils;

public class MainActivity extends Activity implements OnTargetSetListener,
        GoogleApiClient.ConnectionCallbacks {

    public static final String EXTRA_ROUND = "round";
    private TargetSelectView mTarget;
    private DelayedConfirmationView confirm;
    private Round round;
    private GoogleApiClient mGoogleApiClient;

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

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(stub1 -> {
            mTarget = (TargetSelectView) stub1.findViewById(R.id.target);
            confirm = (DelayedConfirmationView) stub1.findViewById(R.id.delayed_confirm);

            // Workaround to avoid crash happening when setting invisible via xml layout
            confirm.setVisibility(View.INVISIBLE);

            // Set up target view
            mTarget.setRoundTemplate(round.info);
            mTarget.reset();
            mTarget.setOnTargetSetListener(MainActivity.this);

            // Ensure Moto 360 is not cut off at the bottom
            stub1.setOnApplyWindowInsetsListener((v, insets) -> {
                int chinHeight = insets.getSystemWindowInsetBottom();
                mTarget.setChinHeight(chinHeight);
                return insets;
            });
        });
    }

    @Override
    public long onTargetSet(final Passe passe, boolean remote) {
        confirm.setVisibility(View.VISIBLE);
        confirm.setTotalTimeMs(2500);
        confirm.start();
        confirm.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerSelected(View view) {
                mTarget.reset();
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
                sendMessage(passe);
            }
        });
        return 0;
    }

    Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    void sendMessage(Passe p) {
        // Serialize bundle to byte array
        byte[] data = new byte[0];
        try {
            data = WearableUtils.serialize(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final byte[] finalData = data;
        new Thread(() -> {
            sendMessage(WearableUtils.FINISHED_INPUT, finalData);
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
