/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import de.dreier.mytargets.fragments.SpinnerDialogFragment;


public class DialogSpinner extends LinearLayout
        implements View.OnClickListener, SpinnerDialogFragment.SpinnerDialogListener {

    private ListAdapter adapter;
    private View mView;

    @StringRes
    private int resTitle;
    private int size;
    private Button addButton;
    private int resAddText;
    private long currentItemId = 0;
    private OnClickListener mListener;

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

    public void setTitle(@StringRes int title) {
        this.resTitle = title;
    }

    public void setAddButton(Button button, @StringRes int text, OnClickListener listener) {
        addButton = button;
        resAddText = text;
        mListener = listener;
        if (addButton != null) {
            addButton.setOnClickListener(mListener);
        }
        updateView();
    }

    private void updateView() {
        int currentSelection = 0;
        for (int i = 0; i < size; i++) {
            if (adapter.getItemId(i) == currentItemId) {
                currentSelection = i;
                break;
            }
        }
        if (size > currentSelection) {
            View tmpView = adapter.getView(currentSelection, mView, this);
            tmpView.setOnClickListener(this);
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateView();
    }

    public long getSelectedItemId() {
        return currentItemId;
    }

    public void setItemId(long id) {
        currentItemId = id;
        updateView();
    }

    @Override
    public void onClick(View v) {
        SpinnerDialogFragment dialog = new SpinnerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("title", resTitle);
        bundle.putInt("add", resAddText);
        dialog.setArguments(bundle);
        dialog.setListener(this);
        dialog.show(((AppCompatActivity) getContext()).getSupportFragmentManager(),
                "spinner_dialog");
    }

    @Override
    public void onDialogConfirmed(int pos) {
        currentItemId = adapter.getItemId(pos);
        updateView();
    }

    @Override
    public void onDialogAdd() {
        mListener.onClick(this);
    }

    @Override
    public ListAdapter getAdapter() {
        return adapter;
    }
}
