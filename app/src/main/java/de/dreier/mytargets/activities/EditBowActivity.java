/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.cocosw.undobar.UndoBarController;
import com.iangclifton.android.floatlabel.FloatLabel;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.DistanceFragment;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.views.DialogSpinner;
import de.dreier.mytargets.views.DistanceDialogSpinner;

public class EditBowActivity extends EditWithImageActivity
        implements UndoBarController.UndoListener {

    public static final String BOW_ID = "bow_id";
    private static final String UNDO_SETTING = "undo_setting";
    private static final String UNDO_SETTING_IND = "undo_setting_ind";
    private static final int REQ_SELECTED_DISTANCE = 1;
    public static final int RECURVE_BOW = 0;
    public static final int COMPOUND_BOW = 1;
    public static final int LONG_BOW = 2;
    public static final int BLANK_BOW = 3;
    public static final int HORSE_BOW = 4;
    public static final int YUMI = 5;
    private FloatLabel name;
    private FloatLabel brand;
    private FloatLabel size;
    private FloatLabel height;
    private FloatLabel tiller;
    private FloatLabel desc;
    private RadioButton recurveBow, compoundBow, longBow, blank, horse, yumi;
    private long mBowId = -1;
    private LinearLayout sight_settings;
    private ArrayList<SightSetting> sightSettingsList;
    private SightSetting curSetting;

    public EditBowActivity() {
        super(R.layout.activity_edit_bow, R.drawable.recurve_bow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BOW_ID)) {
            mBowId = intent.getLongExtra(BOW_ID, -1);
        }

        name = (FloatLabel) findViewById(R.id.name);
        recurveBow = (RadioButton) findViewById(R.id.recurve);
        compoundBow = (RadioButton) findViewById(R.id.compound);
        longBow = (RadioButton) findViewById(R.id.longbow);
        blank = (RadioButton) findViewById(R.id.blank);
        horse = (RadioButton) findViewById(R.id.horse);
        yumi = (RadioButton) findViewById(R.id.yumi);
        brand = (FloatLabel) findViewById(R.id.brand);
        size = (FloatLabel) findViewById(R.id.size);
        height = (FloatLabel) findViewById(R.id.height);
        tiller = (FloatLabel) findViewById(R.id.tiller);
        desc = (FloatLabel) findViewById(R.id.desc);
        sight_settings = (LinearLayout) findViewById(R.id.sight_settings);
        Button add_button = (Button) findViewById(R.id.add_sight_setting_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSightSetting(new SightSetting(), -1);
            }
        });

        recurveBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(RECURVE_BOW);
            }
        });
        compoundBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(COMPOUND_BOW);
            }
        });
        longBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(LONG_BOW);
            }
        });
        blank.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(BLANK_BOW);
            }
        });
        horse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setBowType(HORSE_BOW);
            }
        });
        yumi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               setBowType(YUMI);
            }
        });

        if (savedInstanceState == null && mBowId != -1) {
            DatabaseManager db = DatabaseManager.getInstance(this);
            Bow bow = db.getBow(mBowId, false);
            name.setText(bow.name);
            brand.setText(bow.brand);
            size.setText(bow.size);
            height.setText(bow.height);
            tiller.setText(bow.tiller);
            desc.setText(bow.description);
            imageBitmap = bow.image;
            if (imageBitmap != null) {
                mImageView.setImageBitmap(imageBitmap);
            }
            mImageFile = bow.imageFile;
            setBowType(bow.type);
            sightSettingsList = db.getSettings(mBowId);
        } else if (savedInstanceState == null) {
            recurveBow.setChecked(true);
            sightSettingsList = new ArrayList<>();
        }

        if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString("name"));
            brand.setText(savedInstanceState.getString("brand"));
            size.setText(savedInstanceState.getString("size"));
            height.setText(savedInstanceState.getString("height"));
            tiller.setText(savedInstanceState.getString("tiller"));
            desc.setText(savedInstanceState.getString("desc"));
            sightSettingsList = savedInstanceState.getParcelableArrayList("settings");
        } else {
            if (sightSettingsList.size() == 0 && mBowId == -1) {
                addSightSetting(new SightSetting(), -1);
            } else if (sightSettingsList.size() > 0) {
                for (int i = 0; i < sightSettingsList.size(); i++) {
                    addSightSetting(sightSettingsList.get(i), i);
                }
            }
        }
    }

    @Override
    public void onUndo(@Nullable Parcelable token) {
        if(token!=null) {
            Bundle b = (Bundle) token;
            int ind = b.getInt(UNDO_SETTING_IND);
            SightSetting s = b.getParcelable(UNDO_SETTING);
            sightSettingsList.add(ind, s);
            addSightSetting(s,ind);
        }
    }

    public static class SightSetting implements Parcelable {
        DialogSpinner distance;
        EditText setting;
        public int distanceVal;
        public String value = "";

        public SightSetting() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(distanceVal);
            parcel.writeString(value);
        }

        public static final Parcelable.Creator<SightSetting> CREATOR
                = new Parcelable.Creator<SightSetting>() {
            public SightSetting createFromParcel(Parcel in) {
                return new SightSetting(in);
            }

            public SightSetting[] newArray(int size) {
                return new SightSetting[size];
            }
        };

        private SightSetting(Parcel in) {
            distanceVal = in.readInt();
            value = in.readString();
        }

        public void update() {
            if (distance != null && setting != null) {
                distanceVal = (int) distance.getSelectedItemId();
                value = setting.getText().toString();
            }
        }
    }

    private void addSightSetting(final SightSetting setting, int i) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rel = inflater.inflate(R.layout.sight_settings_item, sight_settings, false);
        setting.distance = (DistanceDialogSpinner) rel.findViewById(R.id.distance_spinner);
        setting.distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditBowActivity.this,
                        ItemSelectActivity.Distance.class);
                i.putExtra("title", R.string.target_round);
                i.putExtra(DistanceFragment.CUR_DISTANCE, setting.distance.getSelectedItemId());
                curSetting = setting;
                startActivityForResult(i, REQ_SELECTED_DISTANCE);
            }
        });
        setting.setting = (EditText) rel.findViewById(R.id.sight_setting);
        ImageButton remove = (ImageButton) rel.findViewById(R.id.remove_sight_setting);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bundle b = new Bundle();
                b.putInt(UNDO_SETTING_IND, sightSettingsList.indexOf(setting));
                b.putParcelable(UNDO_SETTING, setting);
                new UndoBarController.UndoBar(EditBowActivity.this)
                        .message(R.string.sight_setting_removed)
                        .style(UndoBarController.UNDOSTYLE)
                        .token(b)
                        .listener(EditBowActivity.this).show();
                sight_settings.removeView(rel);
                sightSettingsList.remove(setting);
            }
        });
        setting.distance.setItemId(setting.distanceVal);
        setting.setting.setText(setting.value);
        if (i == -1) {
            i = sightSettingsList.size();
            sightSettingsList.add(setting);
        }
        setting.distance.setId(548969458 + i * 2);
        setting.setting.setId(548969458 + i * 2 + 1);
        sight_settings.addView(rel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            long id = data.getLongExtra("id", 0);
            if (requestCode == REQ_SELECTED_DISTANCE) {
                curSetting.distance.setItemId(id);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSave() {
        DatabaseManager db = DatabaseManager.getInstance(this);

        Bow bow = new Bow();
        bow.id = mBowId;
        bow.name = name.getTextString();
        bow.brand = brand.getTextString();
        bow.size = size.getTextString();
        bow.height = height.getTextString();
        bow.tiller = tiller.getTextString();
        bow.description = desc.getTextString();
        bow.type = getType();
        bow.imageFile = mImageFile;
        bow.image = imageBitmap;

        db.updateBow(bow);

        for (SightSetting set : sightSettingsList) {
            set.update();
        }

        db.updateSightSettings(bow.id, sightSettingsList);
        finish();
    }

    private void setBowType(int type) {
        recurveBow.setChecked(type == RECURVE_BOW);
        compoundBow.setChecked(type == COMPOUND_BOW);
        longBow.setChecked(type == LONG_BOW);
        blank.setChecked(type == BLANK_BOW);
        horse.setChecked(type == HORSE_BOW);
        yumi.setChecked(type == YUMI);
    }

    private int getType() {
        if (recurveBow.isChecked()) {
            return RECURVE_BOW;
        } else if (compoundBow.isChecked()) {
            return COMPOUND_BOW;
        } else if (longBow.isChecked()) {
            return LONG_BOW;
        } else if (blank.isChecked()) {
            return BLANK_BOW;
        } else if (horse.isChecked()) {
            return HORSE_BOW;
        } else if (yumi.isChecked()) {
            return YUMI;
        } else {
            return RECURVE_BOW;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", name.getTextString());
        outState.putString("brand", brand.getTextString());
        outState.putString("size", size.getTextString());
        outState.putString("height", height.getTextString());
        outState.putString("tiller", tiller.getTextString());
        outState.putString("desc", desc.getTextString());
        for (SightSetting set : sightSettingsList) {
            set.update();
        }
        outState.putParcelableArrayList("settings", sightSettingsList);
    }
}