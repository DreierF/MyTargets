/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.dreier.mytargets.R;

public class TextInputDialog {

    public static class Builder {
        @StringRes
        private int mTitle;
        private String mDefaultText = "";
        private final Context mContext;
        private OnClickListener mClickListener;

        public Builder(Context context) {
            mContext = context;
        }

        public void show() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.comment_dialog, null);
            final EditText input = (EditText) view.findViewById(R.id.shot_comment);
            input.setText(mDefaultText);

            new AlertDialog.Builder(mContext)
                    .setTitle(mTitle)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mClickListener.onOkClickListener(input.getText().toString());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mClickListener.onCancelClickListener();
                                    dialog.dismiss();
                                }
                            }).show();
        }

        public Builder setDefaultText(String defaultText) {
            mDefaultText = defaultText;
            return this;
        }

        public Builder setTitle(@StringRes int title) {
            this.mTitle = title;
            return this;
        }

        public Builder setOnClickListener(OnClickListener listener) {
            mClickListener = listener;
            return this;
        }
    }

    public static interface OnClickListener {
        public void onCancelClickListener();

        public void onOkClickListener(String input);
    }
}
