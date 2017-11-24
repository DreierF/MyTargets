/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import icepick.Icepick;
import icepick.State;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

public abstract class SelectorBase<T> extends LinearLayout {

    public static final String INDEX = "index";
    private final int layout;
    protected View view;
    protected int requestCode;
    protected Class<?> defaultActivity;
    @Nullable
    @State(ParcelsBundler.class)
    protected T item = null;
    private Button addButton;
    private View progress;
    private OnUpdateListener<T> updateListener;
    private int index = -1;
    private IntentWrapper addIntent;

    public SelectorBase(Context context, AttributeSet attrs, @LayoutRes int layout) {
        super(context, attrs);
        this.layout = layout;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addButton = (Button) getChildAt(0);
        if (addButton != null) {
            addButton.setOnClickListener(v -> addIntent.start());
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        progress = inflater.inflate(R.layout.selector_item_process, this, false);
        view = inflater.inflate(layout, this, false);
        addView(progress);
        addView(view);
        updateView();
    }

    private void updateView() {
        boolean displayProgress = item == null && addButton == null;
        if (addButton != null) {
            addButton.setVisibility(item == null ? VISIBLE : GONE);
        }
        progress.setVisibility(displayProgress ? VISIBLE : GONE);
        view.setVisibility(item != null ? VISIBLE : GONE);
        if (item != null) {
            bindView();
        }
    }

    public void setItemIndex(int index) {
        this.index = index;
    }

    protected IntentWrapper getDefaultIntent() {
        IntentWrapper i = new IntentWrapper(defaultActivity)
                .with(ITEM, Parcels.wrap(getSelectedItem()));
        if (index != -1) {
            i.with(INDEX, index);
        }
        return i;
    }

    @Nullable
    protected IntentWrapper getAddIntent() {
        return null;
    }

    protected abstract void bindView();

    @Nullable
    public T getSelectedItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
        if (updateListener != null) {
            updateListener.onUpdate(item);
        }
        updateView();
    }

    public void setOnUpdateListener(OnUpdateListener<T> updateListener) {
        this.updateListener = updateListener;
    }

    public final void setOnActivityResultContext(@NonNull Fragment fragment) {
        if (addButton != null) {
            addIntent = getAddIntent().withContext(fragment);
        }
        setOnClickListener(v -> getDefaultIntent()
                .withContext(fragment)
                .forResult(requestCode)
                .start());
    }

    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == this.requestCode) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            if (index == -1 || (intentData != null && intentData.getInt(INDEX) == index)) {
                final Parcelable parcelable = data.getParcelableExtra(ITEM);
                setItem(Parcels.unwrap(parcelable));
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    public interface OnUpdateListener<T> {
        void onUpdate(T item);
    }
}
