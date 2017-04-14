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

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ChildActivityBase;
import de.dreier.mytargets.base.gallery.adapters.HorizontalListAdapters;
import de.dreier.mytargets.base.gallery.adapters.ViewPagerAdapter;
import de.dreier.mytargets.databinding.ActivityGalleryBinding;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.EndImage;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.Utils;
import icepick.Icepick;
import icepick.State;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.support.v4.content.FileProvider.getUriForFile;
import static de.dreier.mytargets.base.gallery.GalleryActivityPermissionsDispatcher.onTakePictureWithCheck;

@RuntimePermissions
public class GalleryActivity extends ChildActivityBase {
    public static final String EXTRA_END = "end";

    ViewPagerAdapter adapter;
    LinearLayoutManager layoutManager;
    HorizontalListAdapters previewAdapter;

    @State(ParcelsBundler.class)
    End end;

    private ActivityGalleryBinding binding;

    public static IntentWrapper getIntent(End end) {
        return new IntentWrapper(GalleryActivity.class)
                .with(EXTRA_END, Parcels.wrap(end));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);

        if (savedInstanceState == null) {
            end = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_END));
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        setSupportActionBar(binding.toolbar);

        ToolbarUtils.showHomeAsUp(this);
        ToolbarUtils.setTitle(this, getString(R.string.end_n, end.index + 1));
        Utils.showSystemUI(this);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.imagesHorizontalList.setLayoutManager(layoutManager);

        adapter = new ViewPagerAdapter(this, end.getImages(), binding.toolbar, binding.imagesHorizontalList);
        binding.pager.setAdapter(adapter);

        previewAdapter = new HorizontalListAdapters(this, end.getImages(), this::goToImage);
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

        if (end.getImages().size() == 0 && savedInstanceState == null) {
            onTakePictureWithCheck(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_share).setVisible(!end.getImages().isEmpty());
        menu.findItem(R.id.action_delete).setVisible(!end.getImages().isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        String packageName = getApplicationContext().getPackageName();
        String authority = packageName + ".easyphotopicker.fileprovider";
        EndImage currentEndImage = end.getImages().get(currentItem);
        File file = new File(currentEndImage.getFileName());
        Uri uri = getUriForFile(this, authority, file);
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
                    end.getImages().remove(currentItem);
                    end.save();
                    supportInvalidateOptionsMenu();
                    adapter.notifyDataSetChanged();
                    int nextItem = Math.min(end.getImages().size() - 1, currentItem);
                    previewAdapter.setSelectedItem(nextItem);
                    binding.pager.setCurrentItem(nextItem);
                })
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
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
                            File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getApplicationContext());
                            if (photoFile != null) {
                                photoFile.delete();
                            }
                        }
                    }
                });
    }

    protected void loadImages(final List<File> imageFile) {
        new AsyncTask<Void, Void, List<EndImage>>() {

            @Override
            protected List<EndImage> doInBackground(Void... params) {
                List<EndImage> internalFiles = new ArrayList<>();
                for (File file : imageFile) {
                    try {
                        File internal = File
                                .createTempFile("img", file.getName(), getFilesDir());
                        internalFiles.add(new EndImage(file.getPath()));
                        FileUtils.copy(file, internal);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return internalFiles;
            }

            @Override
            protected void onPostExecute(List<EndImage> files) {
                super.onPostExecute(files);
                end.getImages().addAll(files);
                end.save();
                supportInvalidateOptionsMenu();
                previewAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                int currentPos = end.getImages().size() - 1;
                previewAdapter.setSelectedItem(currentPos);
                binding.pager.setCurrentItem(currentPos);
            }
        }.execute();
    }

    private void goToImage(int pos) {
        if (end.images.size() == pos) {
            onTakePictureWithCheck(this);
        } else {
            binding.pager.setCurrentItem(pos, true);
        }
    }
}
