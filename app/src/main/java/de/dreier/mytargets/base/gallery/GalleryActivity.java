/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.base.gallery;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.evernote.android.state.State;
import com.evernote.android.state.StateSaver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.base.gallery.adapters.HorizontalListAdapters;
import de.dreier.mytargets.base.gallery.adapters.ViewPagerAdapter;
import de.dreier.mytargets.databinding.ActivityGalleryBinding;
import de.dreier.mytargets.shared.models.db.Image;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.shared.utils.FileUtilsKt;
import de.dreier.mytargets.shared.utils.ImageList;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static de.dreier.mytargets.base.gallery.GalleryActivityPermissionsDispatcher.onTakePictureWithPermissionCheck;

@RuntimePermissions
public class GalleryActivity extends ChildActivityBase {
    private static final String RESULT_IMAGES = "images";
    private static final String EXTRA_IMAGES = "images";
    private static final String EXTRA_TITLE = "title";

    ViewPagerAdapter adapter;
    LinearLayoutManager layoutManager;
    HorizontalListAdapters previewAdapter;

    @State
    ImageList imageList;

    private ActivityGalleryBinding binding;

    public static IntentWrapper getIntent(ImageList images, String title) {
        return new IntentWrapper(GalleryActivity.class)
                .with(EXTRA_TITLE, title)
                .with(EXTRA_IMAGES, images);
    }

    public static ImageList getResult(@NonNull Intent data) {
        return data.getParcelableExtra(RESULT_IMAGES);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (savedInstanceState == null) {
            imageList = getIntent().getParcelableExtra(EXTRA_IMAGES);
        } else {
            StateSaver.restoreInstanceState(this, savedInstanceState);
        }

        setSupportActionBar(binding.toolbar);

        ToolbarUtils.showHomeAsUp(this);
        ToolbarUtils.setTitle(this, title);
        Utils.showSystemUI(this);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.imagesHorizontalList.setLayoutManager(layoutManager);

        adapter = new ViewPagerAdapter(this, imageList, binding.toolbar, binding.imagesHorizontalList);
        binding.pager.setAdapter(adapter);

        previewAdapter = new HorizontalListAdapters(this, imageList, this::goToImage);
        binding.imagesHorizontalList.setAdapter(previewAdapter);
        previewAdapter.notifyDataSetChanged();

        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                binding.imagesHorizontalList.smoothScrollToPosition(position);
                previewAdapter.setSelectedItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int currentPos = 0;
        previewAdapter.setSelectedItem(currentPos);
        binding.pager.setCurrentItem(currentPos);

        if (imageList.size() == 0 && savedInstanceState == null) {
            onTakePictureWithPermissionCheck(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_share).setVisible(!imageList.isEmpty());
        menu.findItem(R.id.action_delete).setVisible(!imageList.isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                int currentItem = binding.pager.getCurrentItem();
                shareImage(currentItem);
                return true;
            case R.id.action_delete:
                currentItem = binding.pager.getCurrentItem();
                deleteImage(currentItem);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareImage(int currentItem) {
        Image currentImage = imageList.get(currentItem);
        File file = new File(getFilesDir(), currentImage.getFileName());
        Uri uri = FileUtilsKt.toUri(file, this);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    private void deleteImage(int currentItem) {
        new MaterialDialog.Builder(this)
                .content(R.string.delete_image)
                .negativeText(android.R.string.cancel)
                .negativeColorRes(R.color.md_grey_500)
                .positiveText(R.string.delete)
                .positiveColorRes(R.color.md_red_500)
                .onPositive((dialog, which) -> {
                    imageList.remove(currentItem);
                    updateResult();
                    supportInvalidateOptionsMenu();
                    adapter.notifyDataSetChanged();
                    int nextItem = Math.min(imageList.size() - 1, currentItem);
                    previewAdapter.setSelectedItem(nextItem);
                    binding.pager.setCurrentItem(nextItem);
                })
                .show();
    }

    private void updateResult() {
        setResult(RESULT_OK, wrap(imageList));
    }

    @NonNull
    private Intent wrap(ImageList imageList) {
        Intent i = new Intent();
        i.putExtra(RESULT_IMAGES, imageList);
        return i;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        StateSaver.saveInstanceState(this, outState);
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void onTakePicture() {
        EasyImage.openCamera(this, 0);
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void onSelectImage() {
        EasyImage.openGallery(this, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        GalleryActivityPermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this,
                new DefaultCallback() {

                    @Override
                    public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                        loadImages(imageFiles);
                    }

                    @Override
                    public void onCanceled(EasyImage.ImageSource source, int type) {
                        //Cancel handling, you might wanna remove taken photo if it was canceled
                        if (source == EasyImage.ImageSource.CAMERA) {
                            File photoFile = EasyImage
                                    .lastlyTakenButCanceledPhoto(getApplicationContext());
                            if (photoFile != null) {
                                photoFile.delete();
                            }
                        }
                    }
                });
    }

    protected void loadImages(@NonNull final List<File> imageFile) {
        new AsyncTask<Void, Void, List<String>>() {

            @NonNull
            @Override
            protected List<String> doInBackground(Void... params) {
                List<String> internalFiles = new ArrayList<>();
                for (File file : imageFile) {
                    try {
                        File internal = File.createTempFile("img", file.getName(), getFilesDir());
                        internalFiles.add(internal.getName());
                        FileUtils.INSTANCE.move(file, internal);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return internalFiles;
            }

            @Override
            protected void onPostExecute(@NonNull List<String> files) {
                super.onPostExecute(files);
                imageList.addAll(files);
                updateResult();
                supportInvalidateOptionsMenu();
                previewAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                int currentPos = imageList.size() - 1;
                previewAdapter.setSelectedItem(currentPos);
                binding.pager.setCurrentItem(currentPos);
            }
        }.execute();
    }

    private void goToImage(int pos) {
        if (imageList.size() == pos) {
            onTakePictureWithPermissionCheck(this);
        } else {
            binding.pager.setCurrentItem(pos, true);
        }
    }
}
