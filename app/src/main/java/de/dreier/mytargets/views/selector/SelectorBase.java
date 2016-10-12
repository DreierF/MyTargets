/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import icepick.Icepick;
import icepick.State;

public abstract class SelectorBase<T> extends LinearLayout {

    public static final String INDEX = "index";

    @State(ParcelsBundler.class)
    T item = null;

    protected View view;
    private View progress;
    private final int layout;
    int requestCode;
    Class<?> defaultActivity;
    Class<?> addActivity;
    Button addButton;
    private OnUpdateListener<T> updateListener;
    private int index = -1;

    public SelectorBase(Context context, AttributeSet attrs, @LayoutRes int layout) {
        super(context, attrs);
        this.layout = layout;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addButton = (Button) getChildAt(0);

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        progress = inflater.inflate(R.layout.selector_item_process, this, false);
        view = inflater.inflate(layout, this, false);
        addView(progress);
        addView(view);
        updateView();
    }

    private void updateView() {
        if (addButton != null) {
            addButton.setVisibility(item == null ? VISIBLE : GONE);
        }
        boolean progress = item == null && addButton == null;
        this.progress.setVisibility(progress ? VISIBLE : GONE);
        view.setVisibility(item != null ? VISIBLE : GONE);
        if (item != null) {
            post(this::bindView);
        }
    }

    public void setItemIndex(int index) {
        this.index = index;
    }

    Intent getDefaultIntent() {
        Intent i = new Intent(getContext(), defaultActivity);
        i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(getSelectedItem()));
        if (index != -1) {
            i.putExtra(INDEX, index);
        }
        return i;
    }

    Intent getAddIntent() {
        return new Intent(getContext(), addActivity);
    }

    protected abstract void bindView();

    public T getSelectedItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
        updateView();
        if (updateListener != null) {
            updateListener.onUpdate(item);
        }
    }

    public void setOnUpdateListener(OnUpdateListener<T> updateListener) {
        this.updateListener = updateListener;
    }

    public void setOnActivityResultContext(Fragment fragment) {
        setOnClickListener(v -> new IntentWrapper(fragment, getDefaultIntent())
                .startForResult(requestCode));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == this.requestCode) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            if (index == -1 || (intentData != null && intentData.getInt(INDEX) == index)) {
                final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
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
