/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.utils.ToolbarUtils;

public abstract class EditWithImageFragmentBase extends EditFragmentBase
        implements ObservableScrollViewCallbacks {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    
    @Bind(R.id.imgView)
    ImageView imageView;

    @Bind(R.id.imgProgress)
    ProgressBar imgProgress;

    @Bind(R.id.imgContainer)
    FrameLayout imgContainer;

    @Bind(R.id.overlay)
    View overlay;

    
    @Bind(R.id.scrollView)
    ObservableScrollView scrollView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.title)
    EditText titleView;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    private Uri fileUri;
    String imageFile = null, oldImageFile = null;
    Bitmap imageBitmap = null;

    private final int layoutRes;

    @BindColor(R.color.colorPrimary)
    int mToolbarColor;
    
    @BindDimen(R.dimen.flexible_space_image_height)
    int mFlexibleSpaceImageHeight;
    
    @BindDimen(R.dimen.flexible_space_show_fab_offset)
    int mFlexibleSpaceShowFabOffset;
    
    private boolean fabIsShown;
    
    private final int defaultDrawable;

    @BindDimen(R.dimen.left_space_title_toolbar)
    int mLeftSpace;
    
    @BindDimen(R.dimen.margin_standard)
    int fabMargin;
    private int mActionBarSize;

    public EditWithImageFragmentBase(int layoutRes, int defaultDrawable) {
        this.layoutRes = layoutRes;
        this.defaultDrawable = defaultDrawable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_image, container, false);
        setUpToolbar(rootView);

        // Load values used for image animation
        mActionBarSize = ToolbarUtils.getActionBarSize(getContext());
        int statusBarSize = ToolbarUtils.getStatusBarSize(getContext());

        // Ensure scrollview is at least as big to fill the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LinearLayout content = (LinearLayout) rootView.findViewById(R.id.content);
        content.setMinimumHeight(metrics.heightPixels - mActionBarSize - statusBarSize);

        // Inflate whole layout
        inflater.inflate(layoutRes, content);
        ButterKnife.bind(this, rootView);

        // Set scrollview callbacks
        scrollView.setScrollViewCallbacks(this);

        // Prepare custom title view
        activity.getSupportActionBar().setTitle(null);

        // Initialize FAB button
        registerForContextMenu(fab);
        ViewHelper.setScaleX(fab, 0);
        ViewHelper.setScaleY(fab, 0);
        fab.setOnClickListener(v -> fab.showContextMenu());

        // Initialize layout
        ScrollUtils.addOnGlobalLayoutListener(scrollView, () -> onScrollChanged(0, false, false));

        // Handle saved instance state
        if (savedInstanceState != null) {
            imageBitmap = savedInstanceState.getParcelable("img");
            imageFile = savedInstanceState.getString("image_file");
            imageView.setImageBitmap(imageBitmap);
        } else {
            if (imageBitmap == null) {
                imageView.setImageResource(defaultDrawable);
                imageView.getViewTreeObserver()
                        .addOnGlobalLayoutListener(() -> {
                            if (imageBitmap == null) {
                                int width = imageView.getMeasuredWidth();
                                int height = imageView.getMeasuredHeight();
                                imageBitmap = BitmapUtils
                                        .decodeSampledBitmapFromRes(getContext(), defaultDrawable, width, height);
                            }
                        });
            }
        }
        return rootView;
    }

    public String getName() {
        return titleView.getText().toString();
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    @Override
    public void setTitle(int title) {
        titleView.setText(getString(title));
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - overlay.getHeight();
        ViewHelper.setTranslationY(overlay,
                ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(imgContainer,
                ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(overlay,
                ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, 0.3f);
        titleView.setTextSize(scale * 19);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight -
                titleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        titleTranslationY = Math.max(0, titleTranslationY);
        float scale2 = ScrollUtils
                .getFloat((scrollY - flexibleRange + mActionBarSize) / (mActionBarSize), 0, 1);
        ViewHelper.setTranslationX(titleView, (scale2 * mLeftSpace));
        ViewHelper.setTranslationY(titleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - fab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - fab.getHeight() / 2,
                mActionBarSize - fab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
            lp.leftMargin = overlay.getWidth() - fabMargin - fab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            fab.requestLayout();
        } else {
            ViewHelper
                    .setTranslationX(fab, overlay.getWidth() - fabMargin - fab.getWidth());
            ViewHelper.setTranslationY(fab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        // Change alpha of toolbar background
        if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
            toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mToolbarColor));
        } else {
            toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showFab() {
        if (!fabIsShown) {
            ViewPropertyAnimator.animate(fab).cancel();
            ViewPropertyAnimator.animate(fab).scaleX(1).scaleY(1).setDuration(200).start();
            fabIsShown = true;
        }
    }

    private void hideFab() {
        if (fabIsShown) {
            ViewPropertyAnimator.animate(fab).cancel();
            ViewPropertyAnimator.animate(fab).scaleX(0).scaleY(0).setDuration(200).start();
            fabIsShown = false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
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
                if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = fileUri;
        } else if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data.getData();
        }
        if (selectedImageUri == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();

        new AsyncTask<Uri, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                imgProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected Boolean doInBackground(Uri... params) {
                try {
                    imageBitmap = BitmapUtils.decodeSampledBitmapFromStream(getContext(), params[0],
                            width, height);
                    File f = File.createTempFile("photo", "png", getContext().getFilesDir());
                    FileOutputStream out = new FileOutputStream(f);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    oldImageFile = imageFile;
                    imageFile = f.getName();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    imageView.setImageBitmap(imageBitmap);
                }
                imgProgress.setVisibility(View.GONE);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("img", imageBitmap);
        outState.putString("image_file", imageFile);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
