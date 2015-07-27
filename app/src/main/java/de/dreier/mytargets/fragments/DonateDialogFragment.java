/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import de.dreier.mytargets.adapters.DonationAdapter;


public class DonateDialogFragment extends DialogFragment {

    public static final String DONATION_INFINITE = "donation_infinite";

    public static final ArrayList<String> donations;
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

    public static DonateDialogFragment newInstance(boolean supported, boolean subscribed) {
        DonateDialogFragment frag = new DonateDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("supported", supported && !subscribed);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ListView list = new ListView(getActivity());
        boolean supported = getArguments().getBoolean("supported");
        list.setAdapter(new DonationAdapter(getActivity(), supported));
        final Dialog dialog = new AlertDialog.Builder(getActivity())
                .setView(list)
                .create();
        list.setOnItemClickListener((parent, view, position, id) -> {
            try {
                DonationListener listener = (DonationListener) getTargetFragment();
                listener.onDonate(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return dialog;
    }
}
