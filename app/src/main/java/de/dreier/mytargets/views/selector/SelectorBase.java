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
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.Serializable;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;


public abstract class SelectorBase<T extends Serializable> extends LinearLayout {

    public interface OnUpdateListener<T> {
        void onUpdate(T item);
    }

    final View mView;
    private final View mProgress;
    T item = null;

    private Button mAddButton;
    private OnClickListener onAddClickListener;
    private OnUpdateListener<T> updateListener;

    public SelectorBase(Context context, AttributeSet attrs, @LayoutRes int layout) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mProgress = inflater.inflate(R.layout.item_process, this, false);
        mView = inflater.inflate(layout, this, false);
        addView(mProgress);
        addView(mView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mAddButton = (Button) getChildAt(2);
        if (mAddButton != null) {
            mAddButton.setOnClickListener(onAddClickListener);
        }
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
            bindView();
        }
    }

    protected void setOnClickActivity(Class<?> aClass) {
        setOnClickListener(v -> {
            Intent i = new Intent(getContext(), aClass);
            i.putExtra(ItemSelectActivity.ITEM, item);
            startIntent(i, data -> {
                //noinspection unchecked
                setItem((T) data.getSerializableExtra(ItemSelectActivity.ITEM));
            });
        });
    }

    protected abstract void bindView();

    void setAddButtonIntent(Class<?> addActivity, OnResultListener resultListener) {
        this.onAddClickListener = v -> startIntent(new Intent(getContext(), addActivity), resultListener);
        if (mAddButton != null) {
            mAddButton.setOnClickListener(onAddClickListener);
        }
    }

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

    public interface OnResultListener {
        void onResult(Intent data);
    }

    void startIntent(Intent i, OnResultListener resultListener) {
        final int requestId = (int) (Math.random() * Short.MAX_VALUE);
        final FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
        Fragment auxiliary = new Fragment() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                fm.beginTransaction().remove(this).commit();
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == requestId) {
                        resultListener.onResult(data);
                    }
                }
            }
        };
        fm.beginTransaction().add(auxiliary, "FRAGMENT_TAG").commit();
        fm.executePendingTransactions();
        auxiliary.startActivityForResult(i, requestId);
    }
}
