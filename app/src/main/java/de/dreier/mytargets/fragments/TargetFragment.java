/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;
import com.bignerdranch.android.recyclerviewchoicemode.SingleSelector;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.MyBackupAgent;

public class TargetFragment extends NowListFragment<Target>
        implements SeekBar.OnSeekBarChangeListener {

    final SingleSelector mSingleSelector = new SingleSelector();
    private Spinner scoringStyle;
    private SeekBar seekBar;
    private TextView label;
    private TargetAdapter adapter;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        adapter = new TargetAdapter();
        setList(TargetFactory.getList(getActivity()), adapter);
        mEditable = false;
        mSingleSelector.setSelectable(true);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        scoringStyle = (Spinner) rootView.findViewById(R.id.scoring_style);
        seekBar = (SeekBar) rootView.findViewById(R.id.target_size_seekbar);
        label = (TextView) rootView.findViewById(R.id.target_size_label);
        Target t = (Target) getArguments().getSerializable("item");
        mSingleSelector.setSelected((int) t.getId(), t.getId(), true);
        updateSettings();
        Diameter[] diams = t.getDiameters(getActivity());
        for (int i = 0; i < diams.length; i++) {
            if (diams[i].equals(t.size)) {
                seekBar.setProgress(i);
                break;
            }
        }
        scoringStyle.setSelection(t.scoringStyle);
        seekBar.setOnSeekBarChangeListener(this);
        setHasOptionsMenu(true);
    }

    private void updateSettings() {
        Target target = adapter.getItem(mSingleSelector.getSelectedPosition());
        Diameter[] diameters = target.getDiameters(getActivity());
        if (seekBar.getProgress() > diameters.length - 1) {
            seekBar.setProgress(diameters.length - 1);
        }
        seekBar.setMax(diameters.length - 1);

        // Init target size
        Diameter targetSize = diameters[seekBar.getProgress()];
        label.setText(targetSize.toString());

        // Init scoring styles
        ArrayList<String> styles = target.getScoringStyles();
        //noinspection ConstantConditions
        Context themedContext = ((AppCompatActivity) getActivity()).getSupportActionBar()
                .getThemedContext();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(themedContext,
                android.R.layout.simple_spinner_item, styles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoringStyle.setAdapter(spinnerAdapter);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_target_select;
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (Target) holder.getItem());
    }

    @Override
    public void onClick(SelectableViewHolder holder, Target mItem) {
        if (mItem == null) {
            return;
        }
        mSingleSelector.setSelected(holder, true);
        updateSettings();
    }

    @Override
    protected void onEdit(Target item) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSave();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void onSave() {
        int id = mSingleSelector.getSelectedPosition();
        int scoring = scoringStyle.getSelectedItemPosition();
        Target target = TargetFactory.createTarget(getActivity(), id, scoring);
        Diameter[] diameters = target.getDiameters(getActivity());
        target.size = diameters[seekBar.getProgress()];

        SharedPreferences prefs = getActivity().getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("target", (int) target.getId());
        editor.putInt("scoring", target.scoringStyle);
        editor.putLong("size", target.size.getId());
        editor.apply();

        Intent data = new Intent();
        data.putExtra("item", target);
        getActivity().setResult(Activity.RESULT_OK, data);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateSettings();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    protected class TargetAdapter extends NowListAdapter<Target> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_card_simple, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends SelectableViewHolder<Target> {
        private final TextView mName;
        private final ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView, mSingleSelector, TargetFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
            mImg = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
            mImg.setImageDrawable(mItem);
        }
    }
}
