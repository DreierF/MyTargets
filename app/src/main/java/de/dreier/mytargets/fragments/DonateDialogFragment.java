package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DonationAdapter;

/**
 * Created by Florian on 05.03.2015.
 */
public class DonateDialogFragment extends DialogFragment {

    public static String DONATION_INFINITE = "donation_infinite";

    public static ArrayList<String> donations;
    public static final HashMap<String, String> prices;

    static {
        donations = new ArrayList<>(5);
        donations.add("donation_2");
        donations.add("donation_5");
        donations.add("donation_10");
        donations.add("donation_20");
        donations.add(DONATION_INFINITE);

        prices = new HashMap<>();
    }

    public interface DonationListener {
        void onDonate(int position);
    }

    private DonationListener mListener;

    public static DonateDialogFragment newInstance(boolean supported, boolean subscribed) {
        DonateDialogFragment frag = new DonateDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("supported", supported && !subscribed);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof DonationListener)) {
            throw new IllegalStateException();
        }

        mListener = (DonationListener) activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ListView list = new ListView(getActivity());
        boolean supported = getArguments().getBoolean("supported");
        list.setAdapter(new DonationAdapter(getActivity(), supported));
        final Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.donate_action)
                .setView(list)
                .create();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onDonate(position);
            }
        });
        return dialog;
    }
}
