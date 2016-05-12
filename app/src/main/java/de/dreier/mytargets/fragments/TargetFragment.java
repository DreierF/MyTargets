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

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.TargetFactory;
import de.dreier.mytargets.shared.targets.TargetModelBase;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class TargetFragment extends SelectItemFragment<Target>
        implements SeekBar.OnSeekBarChangeListener {
    public static final String TYPE_FIXED = "type_fixed";

    private boolean typeFixed = false;

    @Bind(R.id.scoring_style)
    Spinner scoringStyleSpinner;

    @Bind(R.id.target_size)
    Spinner targetSizeSpinner;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Process passed arguments
        Target t = Parcels.unwrap(getArguments().getParcelable(ITEM));
        typeFixed = getArguments().getBoolean(TYPE_FIXED);
        List<TargetModelBase> list;
        if (typeFixed) {
            list = TargetFactory.getList(t);
        } else {
            list = TargetFactory.getList();
        }
        List<Target> targets = Stream.of(list)
                .map(value -> new Target((int) value.getId(), 0))
                .collect(Collectors.toList());
        setList(targets, new TargetAdapter());

        int position = targets.indexOf(t);
        mSelector.setSelected(position, t.getId(), true);
        mRecyclerView.scrollToPosition(position);
        updateSettings();

        // Set initial target size
        int diameterIndex = -1;
        Diameter[] diameters = t.getModel().getDiameters();
        for (int i = 0; i < diameters.length; i++) {
            if (diameters[i].equals(t.size)) {
                diameterIndex = i;
                break;
            }
        }
        scoringStyleSpinner.setSelection(t.scoringStyle);
        targetSizeSpinner.setSelection(diameterIndex);
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

        //noinspection ConstantConditions
        Context themedContext = ((AppCompatActivity) getActivity()).getSupportActionBar()
                .getThemedContext();

        // Init scoring styles
        int style = scoringStyleSpinner.getSelectedItemPosition();
        List<String> styles = target.getModel().getScoringStyles();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(themedContext,
                android.R.layout.simple_spinner_item, styles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoringStyleSpinner.setAdapter(spinnerAdapter);
        scoringStyleSpinner.setSelection(style < styles.size() ? style : 0, false);


        // Init target size spinner
        int diameter = targetSizeSpinner.getSelectedItemPosition();
        ArrayList<String> diameters = diameterToList(target.getModel().getDiameters());
        ArrayAdapter<String> diameterSpinnerAdapter = new ArrayAdapter<>(themedContext,
                android.R.layout.simple_spinner_item, diameters);
        diameterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        targetSizeSpinner.setAdapter(diameterSpinnerAdapter);
        if (!typeFixed && diameters.size() > 1) {
            targetSizeSpinner.setVisibility(View.VISIBLE);
        } else {
            targetSizeSpinner.setVisibility(View.GONE);
        }
        targetSizeSpinner.setSelection(diameter < diameters.size() ? diameter : diameters.size() - 1, false);
    }

    private ArrayList<String> diameterToList(Diameter[] diameters) {
        ArrayList<String> list = new ArrayList<>();
        for (Diameter diameter : diameters) {
            list.add(diameter.toString(getContext()));
        }
        return list;
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
        target.scoringStyle = scoringStyleSpinner.getSelectedItemPosition();
        Diameter[] diameters = target.getModel().getDiameters();
        target.size = diameters[targetSizeSpinner.getSelectedItemPosition()];

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
            mName.setText(mItem.getModel().getName(getContext()));
            mImg.setImageDrawable(mItem.getDrawable());
        }
    }
}
