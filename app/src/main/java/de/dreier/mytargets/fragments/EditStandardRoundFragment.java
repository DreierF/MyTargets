/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.DialogSpinner;
import de.dreier.mytargets.views.DistanceDialogSpinner;
import de.dreier.mytargets.views.NumberPicker;

public class EditStandardRoundFragment extends Fragment {
    public static String STANDARD_ROUND_ID = "standard_round_id";
    private static final int REQ_SELECTED_TARGET = 1;
    private static final int REQ_SELECTED_DISTANCE = 2;

    private long mStandardRound = -1;

    private DistanceDialogSpinner distance;
    private RadioButton indoor;
    private DialogSpinner target;
    private NumberPicker rounds, arrows;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_standard_round, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        setHasOptionsMenu(true);

        Bundle i = getArguments();
        if (i != null) {
            mStandardRound = i.getLong(STANDARD_ROUND_ID, -1);
        }
        SharedPreferences prefs = activity.getSharedPreferences(MyBackupAgent.PREFS, 0);

        View scrollView = rootView.findViewById(R.id.scrollView);

        // Distance
        distance = (DistanceDialogSpinner) rootView.findViewById(R.id.distance_spinner);
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ItemSelectActivity.Distance.class);
                i.putExtra("title", R.string.distance);
                i.putExtra(DistanceFragment.CUR_DISTANCE, distance.getSelectedItemId());
                startActivityForResult(i, REQ_SELECTED_DISTANCE);
            }
        });

        // Indoor / outdoor
        RadioButton outdoor = (RadioButton) rootView.findViewById(R.id.outdoor);
        indoor = (RadioButton) rootView.findViewById(R.id.indoor);

        // Show scoreboard
        rounds = (NumberPicker) rootView.findViewById(R.id.rounds);
        rounds.setTextPattern(R.plurals.passe);

        // Points per passe
        arrows = (NumberPicker) rootView.findViewById(R.id.ppp);
        arrows.setTextPattern(R.plurals.arrow);
        arrows.setMinimum(1);
        arrows.setMaximum(10);

        // Target round
        target = (DialogSpinner) rootView.findViewById(R.id.target_spinner);
        target.setAdapter(new TargetItemAdapter(activity));
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,
                        ItemSelectActivity.Target.class);
                i.putExtra("title", R.string.target_round);
                startActivityForResult(i, REQ_SELECTED_TARGET);
            }
        });

        if (mStandardRound == -1) {
            // Initialise with default values
            int dist = prefs.getInt("distance", 10);
            String unit = prefs.getString("unit", "m");
            distance.setItemId(new de.dreier.mytargets.shared.models.Distance(dist, unit).getId());
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            arrows.setValue(prefs.getInt("ppp", 3));
            rounds.setValue(prefs.getInt("rounds", 10));
            target.setItemId(prefs.getInt("target", 2));
        } else {
            // Load saved values
            DatabaseManager db = DatabaseManager.getInstance(activity);
            Round r = db.getRound(mStandardRound);
            distance.setItemId(r.info.distance.getId());
            arrows.setValue(r.info.arrowsPerPasse);
            target.setItemId(r.info.target);

            View not_editable = rootView.findViewById(R.id.not_editable);
            not_editable.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            long id = data.getLongExtra("id", 0);
            if (requestCode == REQ_SELECTED_TARGET) {
                target.setItemId(id);
                return;
            } else if (requestCode == REQ_SELECTED_DISTANCE) {
                distance.setItemId(id);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            getActivity().finish();

                //TODO return as result onBuildStandardRound();
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    StandardRound onBuildStandardRound() {
        StandardRound info = new StandardRound();
        //info.target = (int) target.getSelectedItemId();

        info.setId(mStandardRound);

        //info.distance = de.dreier.mytargets.shared.models.Distance
        //        .fromId(distance.getSelectedItemId());

        //info.arrowsPerPasse = arrows.getValue();
        return info;
    }
}