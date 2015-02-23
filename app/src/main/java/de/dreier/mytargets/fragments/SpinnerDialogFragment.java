package de.dreier.mytargets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.TargetItemAdapter;

/**
 * Created by Florian on 23.02.2015.
 */
public class SpinnerDialogFragment extends DialogFragment {

    public interface SpinnerDialogListener {
        void onDialogConfirmed(int pos);
    }

    private SpinnerDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_launcher);
        String title = "";
        builderSingle.setTitle(title);
        builderSingle.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final TargetItemAdapter arrayAdapter = new TargetItemAdapter(getActivity());
        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        //String strName = arrayAdapter.getItem(which);
                        dialog.dismiss();
                        mListener.onDialogConfirmed(pos);
                    }
                });
        return builderSingle.create();
    }

    public void setListener(SpinnerDialogListener mListener) {
        this.mListener = mListener;
    }
}
