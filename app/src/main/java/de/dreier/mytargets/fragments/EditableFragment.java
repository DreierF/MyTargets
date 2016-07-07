/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.utils.DividerItemDecoration;

/**
 * Shows all rounds of one settings_only day
 */
public abstract class EditableFragment<T extends IIdSettable> extends EditableFragmentBase<T> {

    protected FragmentListBinding binding;
    protected NowListAdapter<T> mAdapter;
    private OnItemSelectedListener listener;

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        mAdapter = getAdapter();
        binding.recyclerView.setAdapter(mAdapter);
        return binding.getRoot();
    }


    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) activity;
        }
        if (getParentFragment() instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) getParentFragment();
        }
    }

    protected abstract NowListAdapter<T> getAdapter();

    protected final void onSelected(T item) {
        if (listener == null) {
            onItemSelected(item);
        } else {
            listener.onItemSelected(Parcels.wrap(item));
        }
    }

    protected abstract void onItemSelected(T item);

    @Override
    protected T getItem(int id) {
        return mAdapter.getItem(id);
    }

    @Override
    protected void addItem(int pos, T item) {
        mAdapter.add(pos, item);
    }

    @Override
    protected void removeItem(int pos) {
        mAdapter.remove(pos);
    }

}
