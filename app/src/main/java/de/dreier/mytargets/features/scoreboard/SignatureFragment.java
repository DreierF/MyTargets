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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.fragments.FragmentBase;
import de.dreier.mytargets.databinding.FragmentSignatureBinding;
import de.dreier.mytargets.shared.models.db.Signature;

import static android.view.View.GONE;

public class SignatureFragment extends FragmentBase {

    private static final String SIGNATURE_ID = "signature_id";
    private static final String DEFAULT_NAME = "default_name";

    private long signatureId;
    private FragmentSignatureBinding binding;
    private Signature signature;
    private String defaultName;

    public static Fragment newInstance(Signature signature, String defaultName) {
        SignatureFragment fragment = new SignatureFragment();
        Bundle args = new Bundle();
        args.putLong(SIGNATURE_ID, signature._id);
        args.putString(DEFAULT_NAME, defaultName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignatureBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        signatureId = args.getLong(SIGNATURE_ID);
        defaultName = args.getString(DEFAULT_NAME);
        return binding.getRoot();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        signature = Signature.get(signatureId);
        return () -> {
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
                binding.signatureLayout.setVisibility(GONE);
                Bitmap bitmap = null;
                if (!binding.signatureView.isEmpty()) {
                    bitmap = binding.signatureView.getTransparentSignatureBitmap();
                }
                signature.bitmap = bitmap;
                signature.save();
                if (getActivity() != null) {
                    ((ScoreboardActivity) getActivity()).back();
                }
            });
            binding.clear.setOnClickListener(v -> binding.signatureView.clear());
        };
    }
}
