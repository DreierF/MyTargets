/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import de.dreier.mytargets.R;

public class TextInputDialog {

    public static class Builder {
        @StringRes
        private int mTitle;
        private String mDefaultText = "";
        private final Context mContext;
        private OnClickListener mClickListener;
        private int mInputType;
        private String[] mChoices;

        public Builder(Context context) {
            mContext = context;
        }

        public void show() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dialog_comment, null);
            final EditText input = (EditText) view.findViewById(R.id.shot_comment);
            input.setInputType(mInputType);
            input.setText(mDefaultText);
            final Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
            if (mChoices != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                        android.R.layout.simple_spinner_item, mChoices);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(0);
                spinner.setVisibility(View.VISIBLE);
            }

            new AlertDialog.Builder(mContext)
                    .setTitle(mTitle)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        String s = input.getText().toString();
                        if (mChoices != null) {
                            s += spinner.getSelectedItem();
                        }
                        mClickListener.onOkClickListener(s);
                        dialog.dismiss();
                    })
                    .setNegativeButton(android.R.string.cancel,
                            (dialog, which) -> {
                                mClickListener.onCancelClickListener();
                                dialog.dismiss();
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

        public Builder setInputType(int type) {
            mInputType = type;
            return this;
        }

        public Builder setSpinnerItems(String[] strings) {
            mChoices = strings;
            return this;
        }
    }

    public interface OnClickListener {
        void onCancelClickListener();

        void onOkClickListener(String input);
    }
}
