package de.dreier.mytargets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowInsets;

import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;

public class MainActivity extends Activity implements TargetSelectView.OnTargetSetListener {

    public TargetSelectView mTarget;
    public DelayedConfirmationView confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTarget = (TargetSelectView)stub.findViewById(R.id.target);
                confirm = (DelayedConfirmationView)stub.findViewById(R.id.delayed_confirm);
                confirm.setVisibility(View.INVISIBLE);
                Round r = new Round();
                r.ppp = 6;
                r.target = 3;
                mTarget.setRoundInfo(r);
                mTarget.switchMode(true);
                mTarget.reset();
                mTarget.setOnTargetSetListener(MainActivity.this);

                stub.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        int chinHeight = insets.getSystemWindowInsetBottom();
                        mTarget.setChinHeight(chinHeight);
                        return insets;
                    }
                });
            }
        });
    }

    @Override
    public void OnTargetSet(Passe passe) {
        confirm.setVisibility(View.VISIBLE);
        confirm.setTotalTimeMs(3500);
        confirm.start();
        confirm.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {
            @Override
            public void onTimerFinished(View view) {
                Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
                intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.saved));
                startActivity(intent);
                finish();
            }

            @Override
            public void onTimerSelected(View view) {
                mTarget.reset();
                confirm.setVisibility(View.INVISIBLE);
            }
        });
    }
}
