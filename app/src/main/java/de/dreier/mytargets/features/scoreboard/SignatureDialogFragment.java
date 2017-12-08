/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.scoreboard;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentSignatureBinding;
import de.dreier.mytargets.shared.models.db.Signature;

public class SignatureDialogFragment extends DialogFragment {

    private static final String ARG_SIGNATURE = "signature_id";
    private static final String ARG_DEFAULT_NAME = "default_name";

    public static SignatureDialogFragment newInstance(Signature signature, String defaultName) {
        SignatureDialogFragment fragment = new SignatureDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SIGNATURE, Parcels.wrap(signature));
        args.putString(ARG_DEFAULT_NAME, defaultName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSignatureBinding binding = FragmentSignatureBinding
                .inflate(inflater, container, false);
        Bundle args = getArguments();
        Signature signature = Parcels.unwrap(args.getParcelable(ARG_SIGNATURE));
        String defaultName = args.getString(ARG_DEFAULT_NAME);

        if (signature.isSigned()) {
            binding.signatureView.setSignatureBitmap(signature.getBitmap());
        }
        binding.editName.setOnClickListener(v -> new MaterialDialog.Builder(getContext())
                .title(R.string.name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(defaultName, signature.name, (dialog, input) -> {
                    signature.name = input.toString();
                    signature.save();
                    binding.signer.setText(signature.name);
                })
                .negativeText(android.R.string.cancel)
                .show());
        binding.signer.setText(signature.getName(defaultName));
        binding.save.setOnClickListener(v -> {
            Bitmap bitmap = null;
            if (!binding.signatureView.isEmpty()) {
                bitmap = binding.signatureView.getTransparentSignatureBitmap();
            }
            signature.bitmap = bitmap;
            signature.save();
            dismiss();
        });
        binding.clear.setOnClickListener(v -> binding.signatureView.clear());
        setCancelable(false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        adjustDialogWidth();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        adjustDialogWidth();
    }

    private void adjustDialogWidth() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
