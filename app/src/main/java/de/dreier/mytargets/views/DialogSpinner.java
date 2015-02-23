package de.dreier.mytargets.views;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.fragments.SpinnerDialogFragment;

/**
 * Created by Florian on 23.02.2015.
 */
public class DialogSpinner extends LinearLayout implements View.OnClickListener, SpinnerDialogFragment.SpinnerDialogListener {

    private TargetItemAdapter adapter;
    private int currentSelection = 0;
    private View mView;

    public DialogSpinner(Context context) {
        super(context);
    }

    public DialogSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(TargetItemAdapter adapter) {
        this.adapter = adapter;
        updateView();
    }

    private void updateView() {
        removeAllViews();
        mView = adapter.getView(currentSelection, mView, this);
        mView.setOnClickListener(this);
        addView(mView);
    }

    public int getSelectedItemPosition() {
        return currentSelection;
    }

    public void setSelection(int sel) {
        currentSelection = sel;
        updateView();
    }

    @Override
    public void onClick(View v) {
        SpinnerDialogFragment dialog = new SpinnerDialogFragment();
        dialog.setListener(this);
        dialog.show(((ActionBarActivity) getContext()).getSupportFragmentManager(), "spinner_dialog");
    }

    @Override
    public void onDialogConfirmed(int pos) {
        setSelection(pos);
    }
}
