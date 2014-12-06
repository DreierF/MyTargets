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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.utils.TargetOpenHelper;
import de.dreier.mytargets.utils.TargetOpenHelper.Passe;
import de.dreier.mytargets.views.TargetView;

public class PasseActivity extends ActionBarActivity implements TargetView.OnTargetSetListener {

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
    private TargetOpenHelper db;
    private boolean mMode = true;
    private TargetOpenHelper.Round r;
    private boolean mShowAllMode = false;

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        Bundle remoteInput = RemoteInput.getResultsFromIntent(i);
        if (remoteInput != null) {
            Log.d("round", "" + mRound);
            String voiceInput = remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString();
            createPasseFromVoiceInput(voiceInput, r);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_targets);

        db = new TargetOpenHelper(this);

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

        updateNotification(null);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(i);
        if (remoteInput != null) {
            String voiceInput = remoteInput.getCharSequence(EXTRA_VOICE_REPLY).toString();
            createPasseFromVoiceInput(voiceInput, r);
        }

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

    private void createPasseFromVoiceInput(String voiceInput, TargetOpenHelper.Round r) {
        Log.d("voice", voiceInput);
        String[] inp = voiceInput.split("( |\\.)");
        Passe passe = new Passe(r.ppp);
        int biggestPoints = Target.getMaxPoints(r.target);
        int cur = 0;
        int color = -1, zone = -1;
        for (int i = 0; i < inp.length; i++) {
            try {
                int points = Integer.parseInt(inp[i]);
                while (points > biggestPoints) {
                    int firstPoints;
                    if (biggestPoints == 10) {
                        firstPoints = Integer.parseInt(inp[i].substring(0, 2));
                        if (firstPoints != 10) {
                            firstPoints = Integer.parseInt(inp[i].substring(0, 1));
                            inp[i] = inp[i].substring(1);
                        } else {
                            inp[i] = inp[i].substring(2);
                        }
                    } else {
                        firstPoints = Integer.parseInt(inp[i].substring(0, 1));
                    }
                    passe.zones[cur] = Target.pointsToZone(r.target, firstPoints);
                    Log.d("passe", "recognized: " + firstPoints);
                    cur++;
                    points = Integer.parseInt(inp[i]);
                }
                Log.d("passe", "recognized: " + points);
                passe.zones[cur] = Target.pointsToZone(r.target, points);
                cur++;
            } catch (NumberFormatException e) {
                String p = inp[i].toLowerCase();
                if (p.equals(getString(R.string.yellow))) {
                    color = 0;
                } else if (p.equals(getString(R.string.gold))) {
                    color = 0;
                } else if (p.equals(getString(R.string.blue))) {
                    color = 1;
                } else if (p.equals(getString(R.string.red))) {
                    color = 2;
                } else if (p.equals(getString(R.string.black))) {
                    color = 3;
                } else if (p.equals(getString(R.string.white))) {
                    color = 4;
                } else if (p.equals(getString(R.string.inner))) {
                    zone = 0;
                } else if (p.equals(getString(R.string.outer))) {
                    zone = 1;
                } else if (p.equals(getString(R.string.mistake))) {
                    Log.d("passe", "recognized: mistake");
                    passe.zones[cur] = -1;
                    cur++;
                } else if (p.startsWith("x")) {
                    Log.d("passe", "recognized: x");
                    passe.zones[cur] = 0;
                    cur++;
                    inp[i] = inp[i].substring(1).trim();
                    if (!inp[i].isEmpty())
                        i--;
                } else {
                    Log.d("passe", "recognized nothing");
                    inp[i] = inp[i].substring(1).trim();
                    if (!inp[i].isEmpty())
                        i--;
                }

                if (color != -1 && zone != -1) {
                    Log.d("passe", "recognized: " + (1 + color * 2 + zone));
                    passe.zones[cur] = 1 + color * 2 + zone;
                    cur++;
                    color = -1;
                    zone = -1;
                }
            }
        }
        if (cur == r.ppp) {
            for (int i = 0; i < r.ppp; i++) {
                passe.points[i][0] = Target.zoneToX(r.target, passe.zones[i]);
                passe.points[i][1] = 0;
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putBoolean(PASSE_VIA_VOICE, true).apply();
            curPasse = savedPasses + 1;
            OnTargetSet(passe);
            setPasse(curPasse);
            updateNotification(passe);
        }
    }

    public void setPasse(int passe) {
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

    public void updatePasse() {
        setTitle("Passe " + curPasse);
        prev.setEnabled(curPasse > 1);
        next.setEnabled(curPasse <= savedPasses);
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
    public void OnTargetSet(Passe passe) {
        TargetOpenHelper db = new TargetOpenHelper(this);

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

    public void updateNotification(Passe passe) {
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
                new NotificationCompat.Action.Builder(R.drawable.ic_action_location_found, "Passe", replyPendingIntent)
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
        TargetOpenHelper.Bow bow = db.getBow(r.bow, false);
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
