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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.iangclifton.android.floatlabel.FloatLabel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Arrow;
import de.dreier.mytargets.utils.BitmapUtils;
import de.dreier.mytargets.views.NotifyingScrollView;

public class EditArrowActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String ARROW_ID = "arrow_id";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    private ImageView mImageView;

    private FloatLabel name,length,material,spine,weight,vanes, point,nock,comment;
    private long mArrowId = -1;
    private String mImageFile = null;
    private Uri fileUri;
    private Bitmap imageBitmap = null;
    private Drawable mActionBarBackgroundDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_arrow);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(ARROW_ID)) {
            mArrowId = intent.getLongExtra(ARROW_ID, -1);
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
        name = (FloatLabel) findViewById(R.id.arrow_name);
        length = (FloatLabel) findViewById(R.id.arrow_length);
        material = (FloatLabel) findViewById(R.id.arrow_material);
        spine = (FloatLabel) findViewById(R.id.arrow_spine);
        weight = (FloatLabel) findViewById(R.id.arrow_weight);
        vanes = (FloatLabel) findViewById(R.id.arrow_vanes);
        point = (FloatLabel) findViewById(R.id.arrow_point);
        nock = (FloatLabel) findViewById(R.id.arrow_nock);
        comment = (FloatLabel) findViewById(R.id.arrow_comment);
        Button cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button newArrow = (Button) findViewById(R.id.new_arrow_button);
        newArrow.setOnClickListener(this);

        if (savedInstanceState == null && mArrowId != -1) {
            DatabaseManager db = new DatabaseManager(this);
            Arrow arrow = db.getArrow(mArrowId, false);
            name.setText(arrow.name);
            length.setText(arrow.length);
            material.setText(arrow.material);
            spine.setText(arrow.spine);
            weight.setText(arrow.weight);
            vanes.setText(arrow.vanes);
            point.setText(arrow.point);
            nock.setText(arrow.nock);
            comment.setText(arrow.comment);
            imageBitmap = arrow.image;
            mImageView.setImageBitmap(imageBitmap);
            mImageFile = arrow.imageFile;
        } else

        if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString("name"));
            length.setText(savedInstanceState.getString("length"));
            material.setText(savedInstanceState.getString("material"));
            spine.setText(savedInstanceState.getString("spine"));
            weight.setText(savedInstanceState.getString("weight"));
            vanes.setText(savedInstanceState.getString("vanes"));
            point.setText(savedInstanceState.getString("point"));
            nock.setText(savedInstanceState.getString("nock"));
            comment.setText(savedInstanceState.getString("comment"));
            imageBitmap = savedInstanceState.getParcelable("img");
            mImageFile = savedInstanceState.getString("image_file");
            mImageView.setImageBitmap(imageBitmap);
        } else {
            if (imageBitmap == null) {
                mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (imageBitmap == null) {
                            int width = mImageView.getMeasuredWidth();
                            int height = mImageView.getMeasuredHeight();
                            imageBitmap = BitmapUtils.decodeSampledBitmapFromRes(EditArrowActivity.this, R.drawable.arrows, width, height);
                        }
                    }
                });
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        DatabaseManager db = new DatabaseManager(this);

        Arrow arrow = new Arrow();
        arrow.id = mArrowId;
        arrow.name = name.getTextString();
        arrow.length = length.getTextString();
        arrow.material = material.getTextString();
        arrow.spine = spine.getTextString();
        arrow.weight = weight.getTextString();
        arrow.vanes = vanes.getTextString();
        arrow.point = point.getTextString();
        arrow.nock = nock.getTextString();
        arrow.comment = comment.getTextString();

        arrow.image = ThumbnailUtils.extractThumbnail(imageBitmap, 100, 100);
        arrow.imageFile = mImageFile;

        mArrowId = db.updateArrow(arrow);
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
                    imageBitmap = BitmapUtils.decodeSampledBitmapFromStream(EditArrowActivity.this, params[0], mImageView.getWidth(), mImageView.getHeight());
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
        outState.putString("length", length.getTextString());
        outState.putString("material", material.getTextString());
        outState.putString("spine", spine.getTextString());
        outState.putString("weight", weight.getTextString());
        outState.putString("vanes", vanes.getTextString());
        outState.putString("point", point.getTextString());
        outState.putString("nock", nock.getTextString());
        outState.putString("comment", comment.getTextString());
        outState.putParcelable("img", imageBitmap);
        outState.putString("image_file", mImageFile);
    }
}