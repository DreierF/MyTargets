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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.utils.BitmapUtils;

public abstract class EditWithImageFragmentBase extends EditFragmentBase {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int SELECT_PICTURE = 2;
    
    @Bind(R.id.imgView)
    ImageView imageView;

    @Bind(R.id.imgProgress)
    ProgressBar imgProgress;

    @Bind(R.id.imgContainer)
    FrameLayout imgContainer;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    private Uri fileUri;
    String imageFile = null, oldImageFile = null;
    Bitmap imageBitmap = null;

    private final int layoutRes;
    
    private final int defaultDrawable;

    EditWithImageFragmentBase(int layoutRes, int defaultDrawable) {
        this.layoutRes = layoutRes;
        this.defaultDrawable = defaultDrawable;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_image, container, false);
        setUpToolbar(rootView);

        NestedScrollView content = (NestedScrollView) rootView.findViewById(R.id.content);

        // Inflate whole layout
        inflater.inflate(layoutRes, content);
        ButterKnife.bind(this, rootView);

        // Prepare custom title view
        setTitle(R.string.new_bow);

        registerForContextMenu(fab);
        fab.setOnClickListener(v -> fab.showContextMenu());

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
