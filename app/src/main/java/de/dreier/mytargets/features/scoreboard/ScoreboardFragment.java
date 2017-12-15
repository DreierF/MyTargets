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

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.fragments.FragmentBase;
import de.dreier.mytargets.databinding.FragmentScoreboardBinding;
import de.dreier.mytargets.databinding.PartialScoreboardSignaturesBinding;
import de.dreier.mytargets.features.scoreboard.pdf.ViewPrintDocumentAdapter;
import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Signature;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.MobileWearableClient;
import de.dreier.mytargets.utils.Utils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static de.dreier.mytargets.shared.utils.FileUtils.getUriForFile;
import static de.dreier.mytargets.utils.MobileWearableClient.BROADCAST_UPDATE_TRAINING_FROM_REMOTE;

public class ScoreboardFragment extends FragmentBase {

    private long trainingId;
    private long roundId;
    private FragmentScoreboardBinding binding;
    private Training training;

    @NonNull
    private BroadcastReceiver updateReceiver = new MobileWearableClient.EndUpdateReceiver() {

        @Override
        protected void onUpdate(Long training, Long round, End end) {
            if (roundId == round || (training == trainingId && roundId == -1)) {
                reloadData();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentScoreboardBinding
                .inflate(inflater, container, false);

        Bundle args = getArguments();
        trainingId = args.getLong(ScoreboardActivity.TRAINING_ID);
        roundId = args.getLong(ScoreboardActivity.ROUND_ID, -1L);
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(updateReceiver,
                new IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_REMOTE));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(updateReceiver);
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        training = Training.Companion.get(trainingId);
        Signature archerSignature = training.getOrCreateArcherSignature();
        Signature witnessSignature = training.getOrCreateWitnessSignature();

        View scoreboard = ScoreboardUtils
                .getScoreboardView(getContext(), Utils.getCurrentLocale(getContext()),
                        training, roundId, SettingsManager.INSTANCE.getScoreboardConfiguration());
        return () -> {
            binding.progressBar.setVisibility(GONE);
            scoreboard
                    .setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            binding.container.removeAllViews();
            binding.container.addView(scoreboard);

            PartialScoreboardSignaturesBinding signatures = PartialScoreboardSignaturesBinding
                    .bind(scoreboard.findViewById(R.id.signatures_layout));

            String archer = SettingsManager.INSTANCE.getProfileFullName();
            if (archer.trim().isEmpty()) {
                archer = getString(R.string.archer);
            }
            String finalArcher = archer;

            signatures.editSignatureArcher
                    .setOnClickListener(view -> onSignatureClicked(archerSignature, finalArcher));
            signatures.editSignatureWitness
                    .setOnClickListener(view -> onSignatureClicked(witnessSignature, getString(R.string.target_captain)));

            signatures.archerSignaturePlaceholder
                    .setVisibility(archerSignature.isSigned() ? GONE : VISIBLE);
            signatures.witnessSignaturePlaceholder
                    .setVisibility(witnessSignature.isSigned() ? GONE : VISIBLE);
        };
    }

    private void onSignatureClicked(Signature signature, String defaultName) {
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            SignatureDialogFragment signatureDialogFragment = SignatureDialogFragment
                    .newInstance(signature, defaultName);
            signatureDialogFragment.show(fm, "signature");
            fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                    fm.unregisterFragmentLifecycleCallbacks(this);
                    reloadData();
                }
            }, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scoreboard, menu);
        menu.findItem(R.id.action_print).setVisible(Utils.isKitKat());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                share();
                return true;
            case R.id.action_print:
                if (Utils.isKitKat()) {
                    print();
                }
                return true;
            case R.id.action_settings:
                SettingsActivity.getIntent(ESettingsScreens.SCOREBOARD)
                        .withContext(this)
                        .start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Called after the user selected with items he wants to share */
    @SuppressLint("StaticFieldLeak")
    private void share() {
        EFileType fileType = SettingsManager.INSTANCE.getScoreboardShareFileType();
        new AsyncTask<Void, Void, Uri>() {

            @Nullable
            @Override
            protected Uri doInBackground(Void... objects) {
                try {
                    File scoreboardFile = new File(getContext()
                            .getCacheDir(), getDefaultFileName(fileType));
                    LinearLayout content = ScoreboardUtils
                            .getScoreboardView(getContext(), Utils
                                    .getCurrentLocale(getContext()), training, roundId, SettingsManager.INSTANCE
                                    .getScoreboardConfiguration());
                    if (fileType == EFileType.PDF && Utils.isKitKat()) {
                        ScoreboardUtils.generatePdf(content, scoreboardFile);
                    } else {
                        ScoreboardUtils
                                .generateBitmap(getContext(), content, scoreboardFile);
                    }

                    return getUriForFile(getContext(), scoreboardFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(@Nullable Uri uri) {
                super.onPostExecute(uri);
                if (uri == null) {
                    Snackbar.make(binding.getRoot(), R.string.sharing_failed, Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    // Build and fire intent to ask for share provider
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType(fileType.mimeType);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void print() {
        String fileName = getDefaultFileName(EFileType.PDF);

        LinearLayout content = ScoreboardUtils.getScoreboardView(getContext(), Utils
                .getCurrentLocale(getContext()), training, roundId, SettingsManager.INSTANCE
                .getScoreboardConfiguration());

        String jobName = getString(R.string.scoreboard) + " Document";
        PrintDocumentAdapter pda = new ViewPrintDocumentAdapter(content, fileName);

        // Create a print job with name and adapter instance
        PrintManager printManager = (PrintManager) getContext()
                .getSystemService(Context.PRINT_SERVICE);
        printManager.print(jobName, pda, new PrintAttributes.Builder().build());
    }

    public String getDefaultFileName(EFileType extension) {
        return String
                .format(Locale.US, "%04d-%02d-%02d-%s.%s", training.getDate().getYear(), training
                        .getDate()
                        .getMonthValue(), training.getDate()
                        .getDayOfMonth(), getString(R.string.scoreboard), extension.name()
                        .toLowerCase());
    }
}
