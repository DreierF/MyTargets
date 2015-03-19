package de.dreier.mytargets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ListAdapter;

/**
 * Created by Florian on 23.02.2015.
 */
public class SpinnerDialogFragment extends DialogFragment {

    public interface SpinnerDialogListener {
        void onDialogConfirmed(int pos);

        void onDialogAdd();

        ListAdapter getAdapter();
    }

    private SpinnerDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        int title = getArguments().getInt("title");
        builderSingle.setTitle(title);

        int add = getArguments().getInt("add");
        if (add != 0) {
            builderSingle.setPositiveButton(add,
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mListener.onDialogAdd();
                                                    dialog.dismiss();
                                                }
                                            });
        }

        builderSingle.setNegativeButton(android.R.string.cancel,
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
        final ListAdapter arrayAdapter = mListener.getAdapter();
        builderSingle.setAdapter(arrayAdapter,
                                 new DialogInterface.OnClickListener() {

                                     @Override
                                     public void onClick(DialogInterface dialog, int pos) {
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
