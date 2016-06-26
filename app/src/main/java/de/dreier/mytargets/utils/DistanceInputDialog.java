/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.InputType;
import android.view.LayoutInflater;

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

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.distance)
                    .setView(binding.getRoot())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        String s = binding.shotComment.getText().toString();
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
