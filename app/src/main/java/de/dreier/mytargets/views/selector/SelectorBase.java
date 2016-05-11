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
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;


public abstract class SelectorBase<T> extends LinearLayout {

    protected int requestCode;
    private final View mView;
    private final View mProgress;
    protected Class<?> defaultActivity;
    protected Class<?> addActivity;
    T item = null;
    protected Button mAddButton;
    private OnUpdateListener<T> updateListener;

    public SelectorBase(Context context, AttributeSet attrs, @LayoutRes int layout) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mProgress = inflater.inflate(R.layout.selector_item_process, this, false);
        mView = inflater.inflate(layout, this, false);
        ButterKnife.bind(this, mView);
        addView(mProgress);
        addView(mView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAddButton = (Button) getChildAt(2);
        updateView();
    }

    private void updateView() {
        if (mAddButton != null) {
            mAddButton.setVisibility(item == null ? VISIBLE : GONE);
        }
        boolean progress = item == null && mAddButton == null;
        setClickable(!progress);
        setEnabled(!progress);
        mProgress.setVisibility(progress ? VISIBLE : GONE);
        mView.setVisibility(item != null ? VISIBLE : GONE);
        if (item != null) {
            post(this::bindView);
        }
    }

    public Intent getDefaultIntent() {
        Intent i = new Intent(getContext(), defaultActivity);
        i.putExtra(ItemSelectActivity.ITEM, Parcels.wrap(item));
        return i;
    }

    public Intent getAddIntent() {
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
        setOnClickListener(v -> {
            fragment.startActivityForResult(getDefaultIntent(), requestCode);
            fragment.getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == this.requestCode) {
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            setItem(Parcels.unwrap(parcelable));
        }
    }

    public interface OnUpdateListener<T> {
        void onUpdate(T item);
    }
}
