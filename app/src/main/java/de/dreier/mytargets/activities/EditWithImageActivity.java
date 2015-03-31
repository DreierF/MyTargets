/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.dreier.mytargets.R;
import de.dreier.mytargets.utils.BitmapUtils;
import de.dreier.mytargets.utils.ToolbarUtils;

public abstract class EditWithImageActivity extends ActionBarActivity
        implements ObservableScrollViewCallbacks {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;

    private Uri fileUri;
    String mImageFile = null;
    Bitmap imageBitmap = null;

    private View mFab;
    private Toolbar mToolbar;
    private View mOverlayView;
    private TextView mTitleView;
    private View mImageContainer;
    ImageView mImageView;
    private ProgressBar mImageProgress;

    private final int layoutRes;
    private int mToolbarColor;
    private boolean mFabIsShown;
    private final int defaultDrawable;

    private int mLeftSpace;
    private int mFabMargin;
    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;
    private int mFlexibleSpaceShowFabOffset;

    public EditWithImageActivity(int layoutRes, int defaultDrawable) {
        this.layoutRes = layoutRes;
        this.defaultDrawable = defaultDrawable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        // Get UI elements
        LinearLayout content = (LinearLayout) findViewById(R.id.content);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mImageContainer = findViewById(R.id.img_container);
        mImageView = (ImageView) findViewById(R.id.img_view);
        mImageProgress = (ProgressBar) findViewById(R.id.img_progress);
        mOverlayView = findViewById(R.id.overlay);
        mTitleView = (TextView) findViewById(R.id.title);
        mFab = findViewById(R.id.fab);
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.scroll);

        // Set up Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        // Load values used for image animation
        mFlexibleSpaceImageHeight = getResources()
                .getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources()
                .getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mLeftSpace = getResources().getDimensionPixelSize(R.dimen.left_space_title_toolbar);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        mActionBarSize = ToolbarUtils.getActionBarSize(this);
        int statusBarSize = ToolbarUtils.getStatusBarSize(this);
        mToolbarColor = getResources().getColor(R.color.colorPrimary);

        // Ensure scrollview is at least as big to fill the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        content.setMinimumHeight(metrics.heightPixels - mActionBarSize - statusBarSize);

        // Inflate whole layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutRes, content);

        // Set scrollview callbacks
        scrollView.setScrollViewCallbacks(this);

        // Prepare custom title view
        mTitleView.setText(getTitle());
        setTitle(null);

        // Initialize FAB button
        registerForContextMenu(mFab);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFab.showContextMenu();
            }
        });

        // Initialize layout
        ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
            @Override
            public void run() {
                onScrollChanged(0, false, false);
            }
        });

        // Handle saved instance state
        if (savedInstanceState != null) {
            imageBitmap = savedInstanceState.getParcelable("img");
            mImageFile = savedInstanceState.getString("image_file");
            mImageView.setImageBitmap(imageBitmap);
        } else {
            if (imageBitmap == null) {
                mImageView.setImageResource(defaultDrawable);
                mImageView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if (imageBitmap == null) {
                                    int width = mImageView.getMeasuredWidth();
                                    int height = mImageView.getMeasuredHeight();
                                    imageBitmap = BitmapUtils
                                            .decodeSampledBitmapFromRes(EditWithImageActivity.this,
                                                    defaultDrawable, width,
                                                    height);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                onSave();
                return true;
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract void onSave();

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView,
                ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageContainer,
                ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView,
                ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, 0.3f);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight -
                mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        titleTranslationY = Math.max(0, titleTranslationY);
        float scale2 = ScrollUtils
                .getFloat((scrollY - flexibleRange + mActionBarSize) / (mActionBarSize), 0, 1);
        ViewHelper.setTranslationX(mTitleView, (scale2 * mLeftSpace));
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper
                    .setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        // Change alpha of toolbar background
        if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mToolbarColor));
        } else {
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
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
                                getString(R.string.select_picture)),
                        SELECT_PICTURE);
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
            protected void onPreExecute() {
                mImageProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Uri... params) {
                try {
                    // Delete old file
                    if (mImageFile != null) {
                        File f = new File(getFilesDir(), mImageFile);
                        //noinspection ResultOfMethodCallIgnored
                        f.delete();
                    }

                    imageBitmap = BitmapUtils
                            .decodeSampledBitmapFromStream(EditWithImageActivity.this, params[0],
                                    mImageView.getWidth(),
                                    mImageView.getHeight());
                    File f = File
                            .createTempFile("png", null, getFilesDir());
                    FileOutputStream out = new FileOutputStream(f);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    mImageFile = f.getName();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    mImageView.setImageBitmap(imageBitmap);
                }
                mImageProgress.setVisibility(View.GONE);
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
        outState.putParcelable("img", imageBitmap);
        outState.putString("image_file", mImageFile);
    }
}
