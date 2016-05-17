/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import de.dreier.mytargets.adapters.DonationAdapter;


public class DonateDialogFragment extends DialogFragment {

    public static final ArrayList<String> donations;
    public static final HashMap<String, String> prices;

    static {
        donations = new ArrayList<>();
        donations.add("donation_2");
        donations.add("donation_5");
        donations.add("donation_10");
        donations.add("donation_20");
        prices = new HashMap<>();
    }

    private DonationListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ListView list = new ListView(getActivity());
        list.setAdapter(new DonationAdapter(getActivity()));
        final Dialog dialog = new AlertDialog.Builder(getActivity())
                .setView(list)
                .create();
        list.setOnItemClickListener((parent, view, position, id) -> listener.onDonate(position));
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DonationListener) {
            listener = (DonationListener) context;
        } else {
            throw new IllegalArgumentException("Parent must conform to protocol DonationListener!");
        }
    }

    public interface DonationListener {
        void onDonate(int position);
    }
}
