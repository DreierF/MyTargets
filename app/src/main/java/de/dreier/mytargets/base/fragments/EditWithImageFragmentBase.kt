/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.base.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.appcompat.widget.PopupMenu
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.evernote.android.state.State
import com.google.android.material.appbar.AppBarLayout
import com.squareup.picasso.Picasso
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.FragmentEditImageBinding
import de.dreier.mytargets.shared.models.Image
import de.dreier.mytargets.shared.models.Thumbnail
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.utils.moveTo
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import timber.log.Timber
import java.io.File
import java.io.IOException

@RuntimePermissions
abstract class EditWithImageFragmentBase<T : Image> protected constructor(
    private val defaultDrawable: Int
) : EditFragmentBase() {

    protected lateinit var binding: FragmentEditImageBinding

    @State
    var imageFile: File? = null

    @State
    var oldImageFile: File? = null

    private lateinit var easyImage: EasyImage

    protected var imageFiles: List<T>
        get() {
            return if (imageFile == null) {
                emptyList()
            } else {
                wrapImage(imageFile!!.name)
            }
        }
        set(images) {
            if (images.isEmpty()) {
                imageFile = null
                binding.imageView.setImageResource(defaultDrawable)
            } else {
                imageFile = File(context!!.filesDir, images[0].fileName)
                if (!imageFile!!.exists()) {
                    imageFile = null
                    binding.imageView.setImageResource(defaultDrawable)
                }
            }
        }

    protected abstract fun wrapImage(imageFile: String): List<T>

    protected val thumbnail: Thumbnail
        get() = if (imageFile == null) {
            Thumbnail.from(context!!, defaultDrawable)
        } else Thumbnail.from(imageFile!!)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        easyImage = EasyImage.Builder(context).build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_image, container, false)
        ToolbarUtils.setSupportActionBar(this, binding.toolbar)
        ToolbarUtils.showUpAsX(this)
        setHasOptionsMenu(true)
        binding.fab.setOnClickListener { this.onFabClicked(it) }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setFocusListenerForAllEditText(view)
        Utils.setupFabTransform(activity!!, binding.root)
    }

    private fun setFocusListenerForAllEditText(view: View?) {
        if (view is ViewGroup) {
            val viewGroup = view as ViewGroup?
            for (i in 0 until viewGroup!!.childCount) {
                setFocusListenerForAllEditText(viewGroup.getChildAt(i))
            }
        } else if (view is EditText) {
            view.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val params = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
                    val behavior = params.behavior as AppBarLayout.Behavior?
                    behavior?.onNestedFling(
                        binding.coordinatorLayout, binding.appBarLayout, v, 0f,
                        v.bottom.toFloat(), true
                    )
                }
            }
        }
    }

    private fun onFabClicked(v: View) {
        val popup = PopupMenu(v.context, v)
        popup.inflate(R.menu.context_menu_image)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_from_gallery -> {
                    onSelectImageWithPermissionCheck()
                    true
                }
                R.id.action_take_picture -> {
                    onTakePictureWithPermissionCheck()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    internal fun onTakePicture() {
        easyImage.openCameraForImage(this)
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun onSelectImage() {
        easyImage.openGallery(this)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage.handleActivityResult(requestCode, resultCode, data, activity!!,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    this@EditWithImageFragmentBase.oldImageFile =
                        this@EditWithImageFragmentBase.imageFile
                    loadImage(imageFiles[0].file)
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) =
                    Timber.e(error)

            })
    }

    protected fun loadImage(imageFile: File?) {
        this.imageFile = imageFile
        if (imageFile == null) {
            binding.imageView.setImageResource(defaultDrawable)
        } else {
            Picasso.with(context)
                .load(imageFile)
                .fit()
                .centerCrop()
                .into(binding.imageView)
        }
    }

    @CallSuper
    override fun onSave() {
        // Delete old file
        oldImageFile?.delete()
        if (imageFile != null) {
            try {
                oldImageFile = imageFile
                imageFile = File
                    .createTempFile("img", oldImageFile!!.name, context!!.filesDir)
                oldImageFile!!.moveTo(imageFile!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
