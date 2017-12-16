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

package de.dreier.mytargets.base.fragments;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.FragmentEditImageBinding;
import de.dreier.mytargets.shared.models.Thumbnail;
import de.dreier.mytargets.shared.models.db.Image;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;
import com.evernote.android.state.State;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static de.dreier.mytargets.base.fragments.EditWithImageFragmentBasePermissionsDispatcher.onSelectImageWithPermissionCheck;
import static de.dreier.mytargets.base.fragments.EditWithImageFragmentBasePermissionsDispatcher.onTakePictureWithPermissionCheck;

@RuntimePermissions
public abstract class EditWithImageFragmentBase<T extends Image> extends EditFragmentBase implements View.OnFocusChangeListener {

    private final int defaultDrawable;
    private final Class<T> clazz;

    protected FragmentEditImageBinding binding;

    @Nullable
    @State
    protected File imageFile = null;

    @Nullable
    @State
    File oldImageFile = null;

    protected EditWithImageFragmentBase(int defaultDrawable, Class<T> clazz) {
        this.defaultDrawable = defaultDrawable;
        this.clazz = clazz;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_image, container, false);
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showUpAsX(this);
        setHasOptionsMenu(true);
        binding.fab.setOnClickListener(this::onFabClicked);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setFocusListenerForAllEditText(getView());
        FabTransformUtil.setup(getActivity(), binding.getRoot());
    }

    private void setFocusListenerForAllEditText(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setFocusListenerForAllEditText(viewGroup.getChildAt(i));
            }
        } else if (view instanceof EditText) {
            view.setOnFocusChangeListener(this);
        }
    }

    @Override
    public void onFocusChange(@NonNull View v, boolean hasFocus) {
        if (hasFocus) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.appBarLayout
                    .getLayoutParams();
            AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
            if (behavior != null) {
                behavior.onNestedFling(binding.coordinatorLayout, binding.appBarLayout, null, 0,
                        v.getBottom(), true);
            }
        }
    }

    private void onFabClicked(@NonNull View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.inflate(R.menu.context_menu_image);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_from_gallery:
                    onSelectImageWithPermissionCheck(this);
                    return true;
                case R.id.action_take_picture:
                    onTakePictureWithPermissionCheck(this);
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
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
        EditWithImageFragmentBasePermissionsDispatcher
                .onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, getActivity(),
                new DefaultCallback() {

                    @Override
                    public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                        EditWithImageFragmentBase.this.oldImageFile = EditWithImageFragmentBase.this.imageFile;
                        loadImage(imageFiles.get(0));
                    }

                    @Override
                    public void onCanceled(EasyImage.ImageSource source, int type) {
                        //Cancel handling, you might wanna remove taken photo if it was canceled
                        if (source == EasyImage.ImageSource.CAMERA) {
                            File photoFile = EasyImage.lastlyTakenButCanceledPhoto(getContext());
                            if (photoFile != null) {
                                photoFile.delete();
                            }
                        }
                    }
                });
    }

    protected void loadImage(@Nullable final File imageFile) {
        this.imageFile = imageFile;
        if (imageFile == null) {
            binding.imageView.setImageResource(defaultDrawable);
        } else {
            Picasso.with(getContext())
                    .load(imageFile)
                    .fit()
                    .centerCrop()
                    .into(binding.imageView);
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
        if (imageFile != null) {
            try {
                oldImageFile = imageFile;
                imageFile = File
                        .createTempFile("img", oldImageFile.getName(), getContext().getFilesDir());
                FileUtils.INSTANCE.copy(oldImageFile, imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setImageFiles(@NonNull List<T> images) {
        if (images.isEmpty()) {
            imageFile = null;
            binding.imageView.setImageResource(defaultDrawable);
        } else {
            imageFile = new File(getContext().getFilesDir(), images.get(0).getFileName());
            if (!imageFile.exists()) {
                imageFile = null;
                binding.imageView.setImageResource(defaultDrawable);
            }
        }
    }

    @NonNull
    protected List<T> getImageFiles() {
        if (imageFile == null) {
            return Collections.emptyList();
        } else {
            T image;
            try {
                image = clazz.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
                return Collections.emptyList();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
            image.setFileName(imageFile.getName());
            return Collections.singletonList(image);
        }
    }

    @NonNull
    protected Thumbnail getThumbnail() {
        if (imageFile == null) {
            return Thumbnail.Companion.from(getContext(), defaultDrawable);
        }
        return Thumbnail.Companion.from(imageFile);
    }
}
