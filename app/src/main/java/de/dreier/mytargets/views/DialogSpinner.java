package de.dreier.mytargets.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import de.dreier.mytargets.fragments.SpinnerDialogFragment;

/**
 * Created by Florian on 23.02.2015.
 */
public class DialogSpinner extends LinearLayout implements View.OnClickListener, SpinnerDialogFragment.SpinnerDialogListener {

    private ListAdapter adapter;
    private int currentSelection = -1;
    private View mView;

    @StringRes
    private int resTitle;
    private int size;
    private Button addButton;
    private Intent addIntent;
    private int resAddText;

    public DialogSpinner(Context context) {
        super(context);
    }

    public DialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(ListAdapter adapter, @StringRes int title) {
        this.adapter = adapter;
        this.resTitle = title;
        this.size = adapter.getCount();
        updateView();
    }

    public void setAddButton(Button button, @StringRes int text, Intent intent) {
        addButton = button;
        resAddText = text;
        addIntent = intent;
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogAdd();
            }
        });
        updateView();
    }

    private void updateView() {
        if (currentSelection >= size) {
            currentSelection = size - 1;
        } else if (currentSelection < 0 && size > 0) {
            currentSelection = 0;
        }
        if (currentSelection > -1) {
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

    public int getSelectedItemPosition() {
        return currentSelection;
    }

    public long getSelectedItemId() {
        if (currentSelection > -1 && currentSelection < size)
            return adapter.getItemId(currentSelection);
        return -1;
    }

    public void setSelection(int sel) {
        currentSelection = sel;
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
        dialog.show(((ActionBarActivity) getContext()).getSupportFragmentManager(), "spinner_dialog");
    }

    @Override
    public void onDialogConfirmed(int pos) {
        setSelection(pos);
    }

    @Override
    public void onDialogAdd() {
        getContext().startActivity(addIntent);
    }

    @Override
    public ListAdapter getAdapter() {
        return adapter;
    }
}
