package de.dreier.mytargets.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.managers.WearMessageManager;
import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.models.OnTargetSetListener;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.views.TargetView;

public class PasseActivity extends ActionBarActivity implements OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";
    private static final String EXTRA_VOICE_REPLY = "voice_input";
    private static final String TARGET_MODE = "target_mode";
    private static final String PASSE_VIA_VOICE = "passe_via_voice";
    private static final String SHOW_ALL_MODE = "show_all";
    private TargetView target;
    private Button next, prev;
    private int curPasse = 1;
    private int savedPasses = 0;
    private long mRound, mTraining;
    private DatabaseManager db;
    private boolean mMode = true;
    private Round r;
    private boolean mShowAllMode = false;
    private WearMessageManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_targets);

        db = new DatabaseManager(this);

        Intent i = getIntent();
        if (i != null && i.hasExtra(ROUND_ID)) {
            mRound = i.getLongExtra(ROUND_ID, -1);
            savedPasses = db.getPasses(mRound).getCount();
            if (i.hasExtra(PASSE_IND)) {
                curPasse = i.getIntExtra(PASSE_IND, -1) - 1;
            } else {
                curPasse = savedPasses + 1;
            }
        }

        target = (TargetView) findViewById(R.id.targetview);
        target.setOnTargetSetListener(this);
        next = (Button) findViewById(R.id.next_button);
        prev = (Button) findViewById(R.id.prev_button);

        r = db.getRound(mRound);
        mTraining = r.training;
        target.setRoundInfo(r);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mMode = prefs.getBoolean(TARGET_MODE, true);
        mShowAllMode = prefs.getBoolean(SHOW_ALL_MODE, false);
        target.switchMode(mMode, false);
        target.showAll(mShowAllMode);
        Bow bow = null;
        if (r.bow != -1) {
            bow = db.getBow(r.bow, true);
        }

        manager = new WearMessageManager(this, r, mMode, bow);

        if (savedInstanceState != null) {
            target.restoreState(savedInstanceState);
            curPasse = savedInstanceState.getInt("curPasse");
            updatePasse();
        } else {
            setPasse(curPasse);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasse(curPasse + 1);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPasse(curPasse - 1);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.passe, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_switch_mode).setIcon(mMode ? R.drawable.ic_target_exact_24dp : R.drawable.ic_target_zone_24dp);
        menu.findItem(R.id.action_show_all).setIcon(mShowAllMode ? R.drawable.ic_visibility_off_white_24dp : R.drawable.ic_visibility_white_24dp);
        return true;
    }

    void setPasse(int passe) {
        if (passe <= savedPasses) {
            Passe p = db.getPasse(mRound, passe);
            if (p != null) {
                target.setZones(p);
            } else {
                target.reset();
            }
        } else if (passe != curPasse) {
            target.reset();
        }
        ArrayList<Passe> oldOnes = db.getRoundPasses(mRound, passe);
        target.setOldShoots(oldOnes);
        curPasse = passe;
        updatePasse();
    }

    void updatePasse() {
        setTitle("Passe " + curPasse);
        prev.setEnabled(curPasse > 1);
        next.setEnabled(curPasse <= savedPasses);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_new_runde:
                i = new Intent(this, NewRoundActivity.class);
                i.putExtra(NewRoundActivity.TRAINING_ID, mTraining);
                i.putExtra(NewRoundActivity.FROM_PASSE, true);
                startActivity(i);
                overridePendingTransition(R.anim.left_in_complete, R.anim.right_out_half);
                return true;
            case R.id.action_switch_mode:
                mMode = !mMode;
                target.switchMode(mMode, true);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean(TARGET_MODE, mMode).apply();
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_show_all:
                mShowAllMode = !mShowAllMode;
                target.showAll(mShowAllMode);
                prefs = PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().putBoolean(SHOW_ALL_MODE, mShowAllMode).apply();
                supportInvalidateOptionsMenu();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curPasse", curPasse);
        target.saveState(outState);
    }

    @Override
    public void onTargetSet(Passe passe) {
        DatabaseManager db = new DatabaseManager(this);

        // Sort passe
        for (int n = passe.zones.length; n > 1; n--) {
            for (int i = 0; i < n - 1; i++) {
                if ((passe.zones[i] > passe.zones[i + 1] && passe.zones[i + 1] != -1) || passe.zones[i] == -1) {
                    int tmp = passe.zones[i];
                    float[] coords = passe.points[i];
                    passe.zones[i] = passe.zones[i + 1];
                    passe.points[i] = passe.points[i + 1];
                    passe.zones[i + 1] = tmp;
                    passe.points[i + 1] = coords;
                }
            }
        }

        if (curPasse > savedPasses) {
            savedPasses++;
            db.addPasseToRound(mRound, passe);
        } else {
            db.updatePasse(mRound, curPasse, passe);
        }
        db.close();
        updatePasse();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    void updateNotification(Passe passe) {
        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(getString(R.string.what_input)).build();

        Intent replyIntent = new Intent(this, PasseActivity.class);
        replyIntent.putExtra(ROUND_ID, mRound);
        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_target_zone_24dp, "Passe", replyPendingIntent)
                        .addRemoteInput(remoteInput).build();

        // This notification will be shown only on watch
        Intent content = new Intent(this, PasseActivity.class);
        content.putExtra(ROUND_ID, mRound);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, content, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap image;
        String title, setting;

        // Initialize message text
        if (passe != null) {
            title = "Passe " + curPasse;
            setting = "";
            for (int i = 0; i < passe.zones.length; i++) {
                setting += Target.getStringByZone(r.target, passe.zones[i], r.compound) + " ";
            }
        } else {
            title = getString(R.string.app_name);
            setting = getString(R.string.swipe_to_left);
        }

        // Load background and bow settings
        Bow bow = db.getBow(r.bow, false);
        if (r.bow == -1 || bow == null) {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.wear_bg);
        } else {
            image = bow.image;
            if (passe != null) {
                setting += "\n";
                setting += db.getSetting(r.bow, r.distanceVal);
            } else {
                setting = db.getSetting(r.bow, r.distanceVal);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(PASSE_VIA_VOICE, false)) {
            setting = getString(R.string.voice_how_to);
        }

        final NotificationCompat.Builder wearableNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(setting)
                .setContentIntent(contentIntent)
                .setGroup("GROUP")
                .setGroupSummary(false)
                .extend(new NotificationCompat.WearableExtender().addAction(action).setBackground(image));

        // Build the notification and issues it with notification manager.
        notificationManager.notify(1, wearableNotificationBuilder.build());
    }
}
