/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.utils.BitmapUtils;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.utils.ThumbnailUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;
import static de.dreier.mytargets.fragments.EditWithImageFragmentBasePermissionsDispatcher.onSelectImageWithCheck;
import static de.dreier.mytargets.fragments.EditWithImageFragmentBasePermissionsDispatcher.onTakePictureWithCheck;

@RuntimePermissions
public abstract class EditWithImageFragmentBase extends EditFragmentBase {

    private final int layoutRes;
    private final int defaultDrawable;

    @Bind(R.id.imgView)
    ImageView imageView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private File imageFile = null;
    private File oldImageFile = null;

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

        return rootView;
    }

    @OnClick(R.id.fab)
    public void onFabClicked(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.context_menu_image);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_from_gallery:
                    onSelectImageWithCheck(this);
                    return true;
                case R.id.action_take_picture:
                    onTakePictureWithCheck(this);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    public void onTakePicture() {
        EasyImage.openCamera(this, 0);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onSelectImage() {
        EasyImage.openGallery(this, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EditWithImageFragmentBasePermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                loadImage(imageFile);
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getContext());
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    protected void loadImage(final File imageFile) {
        this.oldImageFile = this.imageFile;
        this.imageFile = imageFile;
        if (imageFile == null) {
            imageView.setImageResource(defaultDrawable);
        } else {
            Picasso.with(getContext())
                    .load(imageFile)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        }
    }

    protected void loadImage(String path) {
        if (path == null) {
            imageFile = null;
            imageView.setImageResource(defaultDrawable);
        } else {
            imageFile = new File(getContext().getFilesDir(), path);
            if (!imageFile.exists()) {
                imageFile = new File(path);
                if (!imageFile.exists()) {
                    imageFile = null;
                    imageView.setImageResource(defaultDrawable);
                    return;
                }
            }
            Picasso.with(getContext())
                    .load(imageFile)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        }
    }

    @CallSuper
    @Override
    protected void onSave() {
        // Delete old file
        if (oldImageFile != null) {
            File f = oldImageFile;
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }
        try {
            oldImageFile = imageFile;
            imageFile = File.createTempFile("img", oldImageFile.getName(), getContext().getFilesDir());
            BackupUtils.copy(oldImageFile, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    protected String getImageFile() {
        if (imageFile == null) {
            return null;
        } else {
            if (imageFile.getParentFile().equals(getContext().getFilesDir())) {
                return imageFile.getName();
            } else {
                return imageFile.getPath();
            }
        }
    }

    protected byte[] getThumbnail() {
        Bitmap thumbnail;
        if (imageFile == null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), defaultDrawable);
            thumbnail = ThumbnailUtils.extractThumbnail(bitmap,
                    ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL,
                    ThumbnailUtils.TARGET_SIZE_MICRO_THUMBNAIL, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } else {
            thumbnail = ThumbnailUtils.createImageThumbnail(imageFile.getPath(), MICRO_KIND);
        }
        return BitmapUtils.getBitmapAsByteArray(thumbnail);
    }
}
