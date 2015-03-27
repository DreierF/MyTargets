/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.iangclifton.android.floatlabel.FloatLabel;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ArrowItemAdapter;
import de.dreier.mytargets.adapters.BowItemAdapter;
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.fragments.PasseFragment;
import de.dreier.mytargets.fragments.RoundFragment;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.DialogSpinner;
import de.dreier.mytargets.views.DistanceDialogSpinner;
import de.dreier.mytargets.views.NumberPicker;

public class EditRoundActivity extends ActionBarActivity {

    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";
    private long mTraining = -1, mRound = -1;

    private DistanceDialogSpinner distance;
    private RadioButton indoor;
    private DialogSpinner bow;
    private DialogSpinner arrow;
    private DialogSpinner target;
    private int mBowId = 0;
    private EditText training;
    private FloatLabel comment;
    private NumberPicker rounds, arrows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_round);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Intent i = getIntent();
        if (i != null) {
            if (i.hasExtra(TRAINING_ID)) {
                mTraining = i.getLongExtra(TRAINING_ID, -1);
            }
            if (i.hasExtra(ROUND_ID)) {
                mRound = i.getLongExtra(ROUND_ID, -1);
            }
        }
        SharedPreferences prefs = getSharedPreferences(MyBackupAgent.PREFS, 0);

        training = (EditText) findViewById(R.id.training);
        distance = (DistanceDialogSpinner) findViewById(R.id.distance_spinner);

        // Indoor / outdoor
        RadioButton outdoor = (RadioButton) findViewById(R.id.outdoor);
        indoor = (RadioButton) findViewById(R.id.indoor);

        // Show scoreboard
        rounds = (NumberPicker) findViewById(R.id.rounds);
        rounds.setTextPattern(R.plurals.passe);

        // Points per passe
        arrows = (NumberPicker) findViewById(R.id.ppp);
        arrows.setTextPattern(R.plurals.arrow);
        arrows.setMinimum(1);
        arrows.setMaximum(10);

        // Bow
        bow = (DialogSpinner) findViewById(R.id.bow);
        bow.setTitle(R.string.bow);
        bow.setAdapter(new BowItemAdapter(this));
        Button addBow = (Button) findViewById(R.id.add_bow);

        bow.setAddButton(addBow, R.string.add_bow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditRoundActivity.this, EditBowActivity.class));
            }
        });

        // Arrow
        arrow = (DialogSpinner) findViewById(R.id.arrow);
        arrow.setTitle(R.string.arrow);
        arrow.setAdapter(new ArrowItemAdapter(this));
        Button addArrow = (Button) findViewById(R.id.add_arrow);
        arrow.setAddButton(addArrow, R.string.add_arrow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditRoundActivity.this, EditArrowActivity.class));
            }
        });

        // Target round
        target = (DialogSpinner) findViewById(R.id.target_spinner);
        target.setTitle(R.string.target_round);
        target.setAdapter(new TargetItemAdapter(this));

        // Comment
        comment = (FloatLabel) findViewById(R.id.comment);

        if (mRound == -1) {
            // Initialise with default values
            distance.setItemId(prefs.getInt("distance", 10));
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            arrows.setValue(prefs.getInt("ppp", 3));
            rounds.setValue(prefs.getInt("rounds", 10));
            bow.setItemId(prefs.getInt("bow", 0));
            arrow.setItemId(prefs.getInt("arrow", 0));
            target.setItemId(prefs.getInt("target", 2));
            comment.setText("");
        } else {
            // Load saved values
            DatabaseManager db = DatabaseManager.getInstance(this);
            Round r = db.getRound(mRound);
            distance.setItemId(r.distanceVal);
            indoor.setChecked(r.indoor);
            outdoor.setChecked(!r.indoor);
            arrows.setValue(r.ppp);
            bow.setItemId(r.bow);
            arrow.setItemId(r.arrow);
            target.setItemId(r.target);
            comment.setText(r.comment);

            rounds.setEnabled(false);
            arrows.setEnabled(false);
            bow.setEnabled(false);
            arrow.setEnabled(false);
            target.setEnabled(false);
            ((TextView) findViewById(R.id.label_format)).setTextColor(0xff444444);
            ((TextView) findViewById(R.id.label_with)).setTextColor(0xff444444);
            ((TextView) findViewById(R.id.label_bow)).setTextColor(0xff444444);
            ((TextView) findViewById(R.id.label_arrow)).setTextColor(0xff444444);
            ((TextView) findViewById(R.id.label_target)).setTextColor(0xff444444);
        }
        if (mTraining == -1) {
            training.setText(getString(R.string.training));
            getSupportActionBar().setTitle(R.string.new_training);
        } else {
            View training_container = findViewById(R.id.training_container);
            training_container.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bow.setAdapter(new BowItemAdapter(this));
        arrow.setAdapter(new ArrowItemAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void onSave() {
        Round round = new Round();
        round.target = (int) target.getSelectedItemId();

        if (bow.getAdapter().getCount() == 0 && mBowId == 0 && round.target == 3) {
            new AlertDialog.Builder(this).setTitle(R.string.title_compound)
                    .setMessage(R.string.msg_compound_type)
                    .setPositiveButton(R.string.compound_bow,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBowId = -2;
                                    onSave();
                                }
                            })
                    .setNegativeButton(R.string.other_bow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mBowId = -1;
                            onSave();
                        }
                    })
                    .show();
            return;
        }

        DatabaseManager db = DatabaseManager.getInstance(this);

        String title = training.getText().toString();
        if (mTraining == -1) {
            mTraining = db.newTraining(title);
        }

        round.id = mRound;
        round.training = mTraining;
        round.bow = bow.getSelectedItemId();
        round.arrow = arrow.getSelectedItemId();
        if (round.bow == 0) {
            round.bow = mBowId;
        }

        round.distanceVal = (int) distance.getSelectedItemId();
        round.unit = "m";

        int after_rounds = rounds.getValue();
        round.ppp = arrows.getValue();
        round.indoor = indoor.isChecked();
        round.comment = comment.getTextString();
        db.updateRound(round);

        SharedPreferences prefs = getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bow", (int) bow.getSelectedItemId());
        editor.putInt("arrow", (int) arrow.getSelectedItemId());
        editor.putInt("distance", round.distanceVal);
        editor.putInt("ppp", round.ppp);
        editor.putInt("rounds", after_rounds);
        editor.putInt("target", round.target);
        editor.putBoolean("indoor", round.indoor);
        editor.apply();

        finish();
        if (mRound == -1) {
            Intent i = new Intent(this, SimpleFragmentActivity.RoundActivity.class);
            i.putExtra(RoundFragment.TRAINING_ID, mTraining);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(this, SimpleFragmentActivity.PasseActivity.class);
            i.putExtra(PasseFragment.TRAINING_ID, mTraining);
            i.putExtra(PasseFragment.ROUND_ID, round.id);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(this, InputActivity.class);
            i.putExtra(InputActivity.ROUND_ID, round.id);
            i.putExtra(InputActivity.STOP_AFTER, after_rounds);
            startActivity(i);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }
}
