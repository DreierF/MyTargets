package de.dreier.mytargets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;

import de.dreier.mytargets.R;

public class ShareDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    private ShareDialogListener mListener;
    private ArrayList<Integer> mSelectedItems;

    public interface ShareDialogListener {
        void onShareDialogConfirmed(boolean text, boolean dispersion_pattern, boolean scoreboard, boolean comments);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList<>();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.share)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.share_options, null,
                                     new DialogInterface.OnMultiChoiceClickListener() {
                                         @Override
                                         public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                             if (isChecked) {
                                                 // If the user checked the item, add it to the selected items
                                                 mSelectedItems.add(which);
                                             } else if (mSelectedItems.contains(which)) {
                                                 // Else, if the item is already in the array, remove it
                                                 mSelectedItems.remove(Integer.valueOf(which));
                                             }
                                         }
                                     })
                        // Set the action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (!mSelectedItems.isEmpty()) {
                            mListener = (ShareDialogListener) getTargetFragment();
                            mListener.onShareDialogConfirmed(
                                    mSelectedItems.contains(0),
                                    mSelectedItems.contains(1),
                                    mSelectedItems.contains(2),
                                    mSelectedItems.contains(3));
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }
}
