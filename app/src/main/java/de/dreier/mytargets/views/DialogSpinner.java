/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

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


public abstract class DialogSpinner<T extends Serializable> extends LinearLayout {

    public interface OnUpdateListener<T> {
        void onUpdate(T item);
    }

    protected View mView, mProgress;
    protected T item = null;

    private Button addButton;
    private OnUpdateListener<T> updateListener;

    public DialogSpinner(Context context, AttributeSet attrs, @LayoutRes int layout) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mProgress = inflater.inflate(R.layout.item_process, this, false);
        mView = inflater.inflate(layout, this, false);
        addView(mProgress);
        addView(mView);
        updateView();
    }

    private void updateView() {
        if (addButton != null) {
            addButton.setVisibility(item == null ? VISIBLE : GONE);
        }
        mProgress.setVisibility(item == null && addButton == null ? VISIBLE : GONE);
        mView.setVisibility(item != null ? VISIBLE : GONE);
        if (item != null) {
            bindView();
        }
    }

    protected abstract void bindView();

    public void setAddButton(Button button, OnClickListener listener) {
        addButton = button;
        if (addButton != null) {
            addButton.setOnClickListener(listener);
        }
        updateView();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateView();
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

    // TODO use this to handle on click events without the need to define it in every used location
    // TODO possibly refactor the view to become a fragment
    public void startIntent(Intent i, OnResultListener resultListener) {
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
