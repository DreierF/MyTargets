package de.dreier.mytargets.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.iangclifton.android.floatlabel.FloatLabel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.utils.BitmapUtils;
import de.dreier.mytargets.views.NotifyingScrollView;

public class EditBowActivity extends ActionBarActivity {

    public static final String BOW_ID = "bow_id";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    private ImageView mImageView;
    private FloatLabel name, brand, size, height, tiller, desc;
    private RadioButton recurveBow, compoundBow, longBow, blank, horse, yumi;
    private long mBowId = -1;
    private String mImageFile = null;
    private Uri fileUri;
    private Bitmap imageBitmap = null;
    private Drawable mActionBarBackgroundDrawable;
    private LinearLayout sight_settings;
    private ArrayList<SightSetting> sightSettingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bow);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BOW_ID)) {
            mBowId = intent.getLongExtra(BOW_ID, -1);
        }

        mActionBarBackgroundDrawable = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        mActionBarBackgroundDrawable.setAlpha(0);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mActionBarBackgroundDrawable.setCallback(mDrawableCallback);
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(mActionBarBackgroundDrawable);

        ((NotifyingScrollView) findViewById(R.id.scrollView)).setOnScrollChangedListener(mOnScrollChangedListener);

        mImageView = (ImageView) findViewById(R.id.imageView);
        registerForContextMenu(mImageView);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.showContextMenu();
            }
        });
        name = (FloatLabel) findViewById(R.id.bow_name);
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
                recurveBow.setChecked(true);
                compoundBow.setChecked(false);
                longBow.setChecked(false);
                blank.setChecked(false);
                horse.setChecked(false);
                yumi.setChecked(false);
            }
        });

        compoundBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurveBow.setChecked(false);
                compoundBow.setChecked(true);
                longBow.setChecked(false);
                blank.setChecked(false);
                horse.setChecked(false);
                yumi.setChecked(false);
            }
        });

        longBow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurveBow.setChecked(false);
                compoundBow.setChecked(false);
                longBow.setChecked(true);
                blank.setChecked(false);
                horse.setChecked(false);
                yumi.setChecked(false);
            }
        });

        blank.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurveBow.setChecked(false);
                compoundBow.setChecked(false);
                longBow.setChecked(false);
                blank.setChecked(true);
                horse.setChecked(false);
                yumi.setChecked(false);
            }
        });

        horse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurveBow.setChecked(false);
                compoundBow.setChecked(false);
                longBow.setChecked(false);
                blank.setChecked(false);
                horse.setChecked(true);
                yumi.setChecked(false);
            }
        });

        yumi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                recurveBow.setChecked(false);
                compoundBow.setChecked(false);
                longBow.setChecked(false);
                blank.setChecked(false);
                horse.setChecked(false);
                yumi.setChecked(true);
            }
        });

        if (savedInstanceState == null && mBowId != -1) {
            DatabaseManager db = new DatabaseManager(this);
            Bow bow = db.getBow(mBowId, false);
            name.setText(bow.name);
            brand.setText(bow.brand);
            size.setText(bow.size);
            height.setText(bow.height);
            tiller.setText(bow.tiller);
            desc.setText(bow.description);
            imageBitmap = bow.image;
            mImageView.setImageBitmap(imageBitmap);
            mImageFile = bow.imageFile;
            switch (bow.type) {
                case 0:
                    recurveBow.setChecked(true);
                    break;
                case 1:
                    compoundBow.setChecked(true);
                    break;
                case 2:
                    longBow.setChecked(true);
                    break;
                case 3:
                    blank.setChecked(true);
                    break;
                case 4:
                    horse.setChecked(true);
                    break;
                case 5:
                    yumi.setChecked(true);
                    break;
            }
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
            imageBitmap = savedInstanceState.getParcelable("img");
            mImageFile = savedInstanceState.getString("image_file");
            mImageView.setImageBitmap(imageBitmap);
            sightSettingsList = savedInstanceState.getParcelableArrayList("settings");
        } else {
            if (imageBitmap == null) {
                mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (imageBitmap == null) {
                            int width = mImageView.getMeasuredWidth();
                            int height = mImageView.getMeasuredHeight();
                            imageBitmap = BitmapUtils.decodeSampledBitmapFromRes(EditBowActivity.this, R.drawable.recurve_bow, width, height);
                        }
                    }
                });
            }

            if (sightSettingsList.size() == 0 && mBowId == -1) {
                addSightSetting(new SightSetting(), -1);
            } else if(sightSettingsList.size() > 0) {
                for (int i = 0; i < sightSettingsList.size(); i++) {
                    addSightSetting(sightSettingsList.get(i), i);
                }
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SightSetting implements Parcelable {
        Spinner distance;
        View customDist;
        EditText distanceText;
        boolean custom;
        EditText setting;
        public int distanceVal;
        public String value = "/";
        public int distanceInd;

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
            if (custom) {
                distanceVal = Integer.parseInt(distanceText.getText().toString());
            } else {
                distanceVal = NewRoundActivity.distanceValues[distance.getSelectedItemPosition()];
            }
            value = setting.getText().toString();
        }
    }

    private void addSightSetting(final SightSetting setting, int i) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rel = inflater.inflate(R.layout.sight_settings_item, sight_settings, false);
        setting.customDist = rel.findViewById(R.id.customDist);
        setting.distanceText = (EditText) rel.findViewById(R.id.distanceVal);
        setting.distance = (Spinner) rel.findViewById(R.id.distance);
        setting.distance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == NewRoundActivity.distanceValues.length) {
                    setting.distance.setVisibility(View.GONE);
                    setting.customDist.setVisibility(View.VISIBLE);
                    setting.custom = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setting.setting = (EditText) rel.findViewById(R.id.sight_setting);
        ImageButton remove = (ImageButton) rel.findViewById(R.id.remove_sight_setting);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sight_settings.removeView(rel);
                sightSettingsList.remove(setting);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.distances));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setting.distance.setAdapter(adapter);
        if (setting.distanceInd == -1) {
            setting.distance.setVisibility(View.GONE);
            setting.customDist.setVisibility(View.VISIBLE);
            setting.distanceText.setText("" + setting.distanceVal);
            setting.custom = true;
        } else {
            setting.distance.setSelection(setting.distanceInd);
            setting.distance.setVisibility(View.VISIBLE);
            setting.customDist.setVisibility(View.GONE);
            setting.custom = false;
        }
        setting.setting.setText(setting.value);
        if (i == -1) {
            i = sightSettingsList.size();
            sightSettingsList.add(setting);
        }
        setting.distance.setId(548969458 + i * 2);
        setting.setting.setId(548969458 + i * 2 + 1);
        sight_settings.addView(rel);
    }

    public void onSave() {
        DatabaseManager db = new DatabaseManager(this);

        Bow bow = new Bow();
        bow.id = mBowId;
        bow.name = name.getTextString();
        bow.brand = brand.getTextString();
        bow.size = size.getTextString();
        bow.height = height.getTextString();
        bow.tiller = tiller.getTextString();
        bow.description = desc.getTextString();

        if (recurveBow.isChecked())
            bow.type = 0;
        else if (compoundBow.isChecked())
            bow.type = 1;
        else if (longBow.isChecked())
            bow.type = 2;
        else if (blank.isChecked())
            bow.type = 3;
        else if (horse.isChecked())
            bow.type = 4;
        else if (yumi.isChecked())
            bow.type = 5;
        else
            bow.type = 0;

        bow.image = ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
        bow.imageFile = mImageFile;

        mBowId = db.updateBow(bow);

        for (SightSetting set : sightSettingsList)
            set.update();

        db.updateSightSettings(mBowId, sightSettingsList);
        finish();
    }

    private final Drawable.Callback mDrawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
        }
    };

    private final NotifyingScrollView.OnScrollChangedListener mOnScrollChangedListener = new NotifyingScrollView.OnScrollChangedListener() {
        public void onScrollChanged(int t) {
            final int headerHeight = findViewById(R.id.imageView).getHeight() - getSupportActionBar().getHeight();
            final float ratio = (float) Math.min(Math.max(t, 0), headerHeight) / headerHeight;
            final int newAlpha = (int) (ratio * 255);
            mActionBarBackgroundDrawable.setAlpha(newAlpha);
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_image, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_from_gallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.select_picture)), SELECT_PICTURE);
                return true;
            case R.id.action_take_picture:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        fileUri = getOutputMediaFileUri();
                    } catch (NullPointerException e) {
                        return true;
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            selectedImageUri = fileUri;
        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
        } else {
            return;
        }

        new AsyncTask<Uri, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Uri... params) {
                try {
                    imageBitmap = BitmapUtils.decodeSampledBitmapFromStream(EditBowActivity.this, params[0], mImageView.getWidth(), mImageView.getHeight());
                    File f = File.createTempFile(params[0].getLastPathSegment(), null, getFilesDir());
                    FileOutputStream out = new FileOutputStream(f);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    mImageFile = f.getAbsolutePath();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success)
                    mImageView.setImageBitmap(imageBitmap);
            }
        }.execute(selectedImageUri);
    }

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("OutputMediaFile", "SD card is not mounted!");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyTargets");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyTargets", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
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
        outState.putParcelable("img", imageBitmap);
        outState.putString("image_file", mImageFile);
        for (SightSetting set : sightSettingsList)
            set.update();
        outState.putParcelableArrayList("settings", sightSettingsList);
    }
}