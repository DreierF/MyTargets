package de.dreier.mytargets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
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
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.utils.OnTargetSetListener;
import de.dreier.mytargets.utils.Target;
import de.dreier.mytargets.utils.WearableUtils;
import de.dreier.mytargets.views.TargetView;

public class InputActivity extends ActionBarActivity implements OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    private static final String TARGET_MODE = "target_mode";
    private static final String SHOW_ALL_MODE = "show_all";
    public static final String PASSE_IND = "passe_ind";
    public static final String SHOW_SCOREBOARD_AFTER = "show_scoreboard_after";
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
    private int mShowAfter = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        next = (Button) findViewById(R.id.next_button);
        prev = (Button) findViewById(R.id.prev_button);
        target = (TargetView) findViewById(R.id.target_view);
        target.setOnTargetSetListener(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = DatabaseManager.getInstance(this);

        Intent i = getIntent();
        if (i != null && i.hasExtra(ROUND_ID)) {
            mRound = i.getLongExtra(ROUND_ID, -1);
            mShowAfter = i.getIntExtra(SHOW_SCOREBOARD_AFTER, -1);
            savedPasses = db.getPasses(mRound).size();
            if (i.hasExtra(PASSE_IND)) {
                curPasse = i.getIntExtra(PASSE_IND, -1);
            } else {
                curPasse = savedPasses + 1;
            }
        }

        r = db.getRound(mRound);
        mTraining = r.training;
        target.setRoundInfo(r);
        mMode = prefs.getBoolean(TARGET_MODE, true);
        mShowAllMode = prefs.getBoolean(SHOW_ALL_MODE, false);
        target.switchMode(mMode, false);
        target.showAll(mShowAllMode);

        // Send message to wearable app, that we are starting a passe
        new Thread(new Runnable() {
            @Override
            public void run() {
                startWearNotification();
            }
        }).start();

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

    private void startWearNotification() {
        Bitmap image;
        if (r.bow > 0) {
            Bow bow = DatabaseManager.getInstance(this).getBow(r.bow, true);
            image = bow.image;
        } else {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.wear_bg);
        }
        image = ThumbnailUtils.extractThumbnail(image, 320, 320);

        WearableUtils.NotificationInfo info = buildInfo();
        manager = new WearMessageManager(this, image, info);
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
        menu.findItem(R.id.action_switch_mode)
                .setIcon(mMode ? R.drawable.ic_target_exact_24dp : R.drawable.ic_target_zone_24dp);
        menu.findItem(R.id.action_show_all).setIcon(
                mShowAllMode ? R.drawable.ic_visibility_off_white_24dp :
                        R.drawable.ic_visibility_white_24dp);
        return true;
    }

    void setPasse(int passe) {
        if (passe <= savedPasses) {
            Passe p = db.getPasse(mRound, passe);
            if (p != null) {
                target.setPasse(p);
            } else {
                target.reset();
            }
        } else if (passe != curPasse) {
            target.reset();
        }
        ArrayList<Passe> oldOnes = db.getPasses(mRound);
        target.setOldShoots(oldOnes);
        curPasse = passe;
        updatePasse();
    }

    void updatePasse() {
        setTitle(getString(R.string.passe_n, curPasse));
        prev.setEnabled(curPasse > 1);
        next.setEnabled(curPasse <= savedPasses);
        if (savedPasses == mShowAfter) {
            Intent intent = new Intent(this, ScoreboardActivity.class);
            intent.putExtra(ScoreboardActivity.ROUND_ID, mRound);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.action_new_round:
                i = new Intent(this, EditRoundActivity.class);
                i.putExtra(EditRoundActivity.TRAINING_ID, mTraining);
                i.putExtra(EditRoundActivity.FROM_PASSE, true);
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
    public long onTargetSet(Passe passe, boolean remote) {
        passe.sort();

        db.updatePasse(mRound, passe);
        if (curPasse > savedPasses || remote) {
            savedPasses++;
            manager.sendMessage(buildInfo());
            if (remote) {
                curPasse = savedPasses + 1;
            }
        } else if (curPasse == savedPasses) {
            manager.sendMessage(buildInfo());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePasse();
            }
        });
        return passe.id;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private WearableUtils.NotificationInfo buildInfo() {
        String title = getString(R.string.passe_n, savedPasses);
        String text = "";

        // Initialize message text
        Passe lastPasse = db.getPasse(mRound, savedPasses);
        if (lastPasse != null) {
            for (Shot shot : lastPasse.shot) {
                text += Target.getStringByZone(r.target, shot.zone) + " ";
            }
            text += "\n";
        } else {
            title = getString(R.string.app_name);
        }

        // Load bow settings
        if (r.bow > 0) {
            text += r.distance + ": " + db.getSetting(r.bow, r.distanceVal);
        }
        return new WearableUtils.NotificationInfo(r, title, text);
    }
}
