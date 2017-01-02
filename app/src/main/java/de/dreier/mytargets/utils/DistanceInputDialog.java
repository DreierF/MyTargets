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

package de.dreier.mytargets.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
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
        private final Context mContext;
        private OnClickListener mClickListener;
        private String mUnit;

        public Builder(Context context) {
            mContext = context;
        }

        public void show() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            DialogCommentBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_comment, null, false);
            binding.shotComment.setInputType(InputType.TYPE_CLASS_NUMBER);
            binding.unit.setText(mUnit);
            final EditText shotComment = binding.shotComment;

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.distance)
                    .setView(binding.getRoot())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        String s = shotComment.getText().toString();
                        mClickListener.onOkClickListener(s);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss()).show();
        }

        public Builder setOnClickListener(OnClickListener listener) {
            mClickListener = listener;
            return this;
        }

        public Builder setUnit(String unit) {
            mUnit = unit;
            return this;
        }
    }
}
