
package com.michaelflisar.licenses.dialog;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

import com.michaelflisar.licenses.SupportDialogFragment;
import com.michaelflisar.licenses.licenses.LicenseEntry;
import com.michaelflisar.licensesdialog.R;

public class LicensesDialog extends DialogPreference implements OnClickListener, SupportDialogFragment {

	private final String BASE_TAG = LicensesDialog.class.getName();

    private AdapterLicenses mAdapter = null;
    private List<LicenseEntry> mLicenses;

    public LicensesDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

    public void setLicences(List<LicenseEntry> licenses) {
        mLicenses = licenses;
    }

    @Override
    protected void onRestoreInstanceState (Parcelable state) {
    	Bundle savedInstanceState = (Bundle) state;
        if (savedInstanceState != null) {
            int licenses = savedInstanceState.getInt(BASE_TAG + "mLicenses|size");
            mLicenses = new ArrayList<LicenseEntry>();
            for (int i = 0; i < licenses; i++)
                mLicenses.add((LicenseEntry) savedInstanceState.getParcelable(BASE_TAG + "mLicenses|" + i));
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle outState = new Bundle();
        outState.putInt(BASE_TAG + "mLicenses|size", mLicenses.size());
        for (int i = 0; i < mLicenses.size(); i++)
            outState.putParcelable(BASE_TAG + "mLicenses|" + i, mLicenses.get(i));
        return outState;
    }

    @Override
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(R.string.ld_licenses);
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton("", this);
    }

    @Override
    public View onCreateDialogView() {
        mAdapter = new AdapterLicenses(getContext(), mLicenses);
        ExpandableListView expendableList = new ExpandableListView(getContext());
        expendableList.setAdapter(mAdapter);
        return expendableList;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

	@Override
	public void show(Bundle savedInstanceState) {
		showDialog(savedInstanceState);
	}

	@Override
	public void onSaveDialogInstanceState(Bundle out) {
		out.putBundle("licences", (Bundle) onSaveInstanceState());
	}

	@Override
	public boolean isShowing() {
		if(getDialog()!=null) {
			return getDialog().isShowing();
		} else {
			return false;
		}
	}
}
