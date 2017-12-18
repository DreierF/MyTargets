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

package de.dreier.mytargets.features.distance;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.view.LayoutInflater;
import android.widget.EditText;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.DialogCommentBinding;

public class DistanceInputDialog {

    public interface OnClickListener {
        void onOkClickListener(String input);
    }

    public static class Builder {
        private final Context context;
        private OnClickListener clickListener;
        private String unit;

        public Builder(Context context) {
            this.context = context;
        }

        public void show() {
            LayoutInflater inflater = LayoutInflater.from(context);
            DialogCommentBinding binding = DataBindingUtil
                    .inflate(inflater, R.layout.dialog_comment, null, false);
            binding.shotComment.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.unit.setText(unit);
            final EditText shotComment = binding.shotComment;

            new AlertDialog.Builder(context)
                    .setTitle(R.string.distance)
                    .setView(binding.getRoot())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        String s = shotComment.getText().toString();
                        clickListener.onOkClickListener(s);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .show();
        }

        @NonNull
        public Builder setOnClickListener(OnClickListener listener) {
            clickListener = listener;
            return this;
        }

        @NonNull
        public Builder setUnit(String unit) {
            this.unit = unit;
            return this;
        }
    }
}
