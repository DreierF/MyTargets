/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class TargetFragment extends SelectItemFragment<Target>
        implements SeekBar.OnSeekBarChangeListener {

    public static final String TYPE_FIXED = "type_fixed";
    private Spinner scoringStyle;
    private SeekBar seekBar;
    private TextView label;
    private boolean typeFixed = false;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Process passed arguments
        Target t = (Target) getArguments().getSerializable(ITEM);
        typeFixed = getArguments().getBoolean(TYPE_FIXED);
        List<Target> list;
        if (typeFixed) {
            list = TargetFactory.getList(getActivity(), t);
        } else {
            list = TargetFactory.getList(getActivity());
        }
        setList(list, new TargetAdapter());

        scoringStyle = (Spinner) rootView.findViewById(R.id.scoring_style);
        seekBar = (SeekBar) rootView.findViewById(R.id.target_size_seekbar);
        label = (TextView) rootView.findViewById(R.id.target_size_label);
        int position = list.indexOf(t);
        mSelector.setSelected(position, t.getId(), true);
        mRecyclerView.scrollToPosition(position);
        updateSettings();

        // Set initial target size
        Diameter[] diameters = t.getDiameters();
        for (int i = 0; i < diameters.length; i++) {
            if (diameters[i].equals(t.size)) {
                seekBar.setProgress(i);
                label.setText(t.size.toString(getActivity()));
                break;
            }
        }
        scoringStyle.setSelection(t.scoringStyle);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(SelectableViewHolder holder, Target mItem) {
        super.onClick(holder, mItem);
        if (mItem == null) {
            return;
        }
        updateSettings();
    }

    private void updateSettings() {
        Target target = mAdapter.getItem(mSelector.getSelectedPosition());
        Diameter[] diameters = target.getDiameters();
        if (seekBar.getProgress() > diameters.length - 1) {
            seekBar.setProgress(diameters.length - 1);
        }
        seekBar.setMax(diameters.length - 1);
        if (!typeFixed && diameters.length > 1) {
            seekBar.setVisibility(View.VISIBLE);
        } else {
            seekBar.setVisibility(View.GONE);
        }

        // Init target size
        Diameter targetSize = diameters[seekBar.getProgress()];
        label.setText(targetSize.toString(getActivity()));

        // Init scoring styles
        int style = scoringStyle.getSelectedItemPosition();
        ArrayList<String> styles = target.getScoringStyles();
        //noinspection ConstantConditions
        Context themedContext = ((AppCompatActivity) getActivity()).getSupportActionBar()
                .getThemedContext();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(themedContext,
                android.R.layout.simple_spinner_item, styles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoringStyle.setAdapter(spinnerAdapter);
        if (styles.size() > 1) {
            scoringStyle.setVisibility(View.VISIBLE);
        } else {
            scoringStyle.setVisibility(View.GONE);
        }
        scoringStyle.setSelection(style < styles.size() ? style : 0, false);
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
    protected Target onSave() {
        Target target = super.onSave();
        int scoring = scoringStyle.getSelectedItemPosition();
        target = TargetFactory.createTarget(getActivity(), target.id, scoring);
        Diameter[] diameters = target.getDiameters();
        target.size = diameters[seekBar.getProgress()];

        SharedPreferences prefs = getActivity().getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("target", (int) target.getId());
        editor.putInt("scoring", target.scoringStyle);
        editor.putLong("size", target.size.getId());
        editor.apply();
        return target;
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

    private class TargetAdapter extends NowListAdapter<Target> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_image_simple, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<Target> {
        private final TextView mName;
        private final ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, TargetFragment.this);
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
