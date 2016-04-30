/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.dreier.mytargets.R;

public class DistanceInputDialog {

    public static class Builder {
        private final Context mContext;
        private OnClickListener mClickListener;
        private String mUnit;

        public Builder(Context context) {
            mContext = context;
        }

        public void show() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_comment, null);
            final EditText input = (EditText) view.findViewById(R.id.shot_comment);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            final TextView unit = (TextView) view.findViewById(R.id.unit);
            unit.setText(mUnit);

            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.distance)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        String s = input.getText().toString();
                        mClickListener.onOkClickListener(s);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.cancel,
                            (dialog, which) -> {
                                dialog.dismiss();
                            }).show();
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

    public interface OnClickListener {
        void onOkClickListener(String input);
    }
}
