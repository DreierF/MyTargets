package de.dreier.mytargets.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import de.dreier.mytargets.models.OnTargetSetListener;
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.models.WearableUtils;
import de.dreier.mytargets.views.TargetView;

public class PasseActivity extends ActionBarActivity implements OnTargetSetListener {

    public static final String ROUND_ID = "round_id";
    public static final String PASSE_IND = "passe_ind";
    private static final String TARGET_MODE = "target_mode";
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

        target = (TargetView) findViewById(R.id.target_view);
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
        Bitmap image;
        if (r.bow > -1) {
            Bow bow = db.getBow(r.bow, true);
            image = bow.image;
        } else {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.wear_bg);
        }

        WearableUtils.NotificationInfo info = buildInfo();
        manager = new WearMessageManager(this, image, info);

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
            case R.id.action_new_round:
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
                manager.sendMessage(buildInfo());
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
    public void onTargetSet(Passe passe, boolean remote) {
        DatabaseManager db = new DatabaseManager(this);
        passe.sort();

        if (curPasse > savedPasses || remote) {
            savedPasses++;
            db.addPasseToRound(mRound, passe);
            manager.sendMessage(buildInfo());
            if (remote)
                curPasse = savedPasses + 1;
        } else {
            db.updatePasse(mRound, curPasse, passe);
            if (curPasse == savedPasses) {
                manager.sendMessage(buildInfo());
            }
        }
        db.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updatePasse();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private WearableUtils.NotificationInfo buildInfo() {
        String title = "Passe " + savedPasses;
        String text = "";

        // Initialize message text
        Passe lastPasse = db.getPasse(mRound, savedPasses);
        if (lastPasse != null) {
            for (int zone : lastPasse.zones) {
                text += Target.getStringByZone(r.target, zone, r.compound) + " ";
            }
            text += "\n";
        } else {
            text = getString(R.string.app_name);
        }

        // Load bow settings
        if (r.bow > -1) {
            text += db.getSetting(r.bow, r.distanceVal);
        }
        return new WearableUtils.NotificationInfo(r, title, text, mMode);
    }
}
