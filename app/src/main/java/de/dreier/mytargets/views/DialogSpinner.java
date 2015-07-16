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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;


public class DialogSpinner extends LinearLayout {

    private ListAdapter adapter;
    private View mView;

    private int size;
    private Button addButton;
    private long currentItemId = 0;
    private OnClickListener listener;
    private OnResultListener resultListener;

    public DialogSpinner(Context context) {
        super(context);
    }

    public DialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        size = adapter.getCount();
        if (size == 0) {
            currentItemId = 0;
        } else {
            currentItemId = adapter.getItemId(0);
        }
        updateView();
    }

    private void updateView() {
        int currentSelection = getCurrentSelection();
        if (size > currentSelection) {
            View tmpView = adapter.getView(currentSelection, mView, this);
            tmpView.setOnClickListener(listener);
            tmpView.setEnabled(isEnabled());
            if (mView == null) {
                mView = tmpView;
                addView(mView);
            }
        }
        if (addButton != null) {
            if (size > 0) {
                addButton.setVisibility(GONE);
            } else {
                addButton.setVisibility(VISIBLE);
                addButton.setEnabled(isEnabled());
            }
        }
    }

    private int getCurrentSelection() {
        int currentSelection = 0;
        for (int i = 0; i < size; i++) {
            if (adapter.getItemId(i) == currentItemId) {
                currentSelection = i;
                break;
            }
        }
        return currentSelection;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        listener = l;
    }

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

    public long getSelectedItemId() {
        return currentItemId;
    }

    public Object getSelectedItem() {
        return adapter.getItem(getCurrentSelection());
    }

    public void setItemId(long id) {
        currentItemId = id;
        updateView();
    }

    public ListAdapter getAdapter() {
        return adapter;
    }

    public void setOnResultListener(OnResultListener listener) {
        resultListener = listener;
    }

    public interface OnResultListener {
        void onResult(Intent data);
    }

    // TODO use this to handle on click events without the need to define it in every used location
    // TODO possibly refactor the view to become a fragment
    public void startIntent(Intent i) {
        final FragmentManager fm = ((FragmentActivity) getContext()).getSupportFragmentManager();
        final int id = (int) (Math.random() * Short.MAX_VALUE);
        Fragment auxiliary = new Fragment() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                fm.beginTransaction().remove(this).commit();
                if (resultCode == Activity.RESULT_OK) {
                    if (requestCode == id) {
                        resultListener.onResult(data);
                    }
                }
            }
        };
        fm.beginTransaction().add(auxiliary, "FRAGMENT_TAG").commit();
        fm.executePendingTransactions();
        auxiliary.startActivityForResult(i, id);
    }
}
