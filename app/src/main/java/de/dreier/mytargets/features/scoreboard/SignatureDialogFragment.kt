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

package de.dreier.mytargets.features.scoreboard

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.databinding.FragmentSignatureBinding
import de.dreier.mytargets.shared.models.db.Signature

class SignatureDialogFragment : DialogFragment() {

    private val signatureDAO = ApplicationInstance.db.signatureDAO()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignatureBinding.inflate(inflater, container, false)
        val args = arguments
        val signature = args!!.getParcelable<Signature>(ARG_SIGNATURE)
        val defaultName = args.getString(ARG_DEFAULT_NAME)

        if (signature!!.isSigned) {
            binding.signatureView.signatureBitmap = signature.bitmap
        }
        binding.editName.setOnClickListener {
            MaterialDialog.Builder(context!!)
                .title(R.string.name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(defaultName, signature.name) { _, input ->
                    signature.name = input.toString()
                    signatureDAO.updateSignature(signature)
                    binding.signer.text = signature.name
                }
                .negativeText(android.R.string.cancel)
                .show()
        }
        binding.signer.text = signature.getName(defaultName!!)
        binding.save.setOnClickListener {
            var bitmap: Bitmap? = null
            if (!binding.signatureView.isEmpty) {
                bitmap = binding.signatureView.transparentSignatureBitmap
            }
            signature.bitmap = bitmap
            signatureDAO.updateSignature(signature)
            dismiss()
        }
        binding.clear.setOnClickListener { binding.signatureView.clear() }
        isCancelable = false
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adjustDialogWidth()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        adjustDialogWidth()
    }

    private fun adjustDialogWidth() {
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        private const val ARG_SIGNATURE = "signature_id"
        private const val ARG_DEFAULT_NAME = "default_name"

        fun newInstance(signature: Signature, defaultName: String): SignatureDialogFragment {
            val fragment = SignatureDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_SIGNATURE, signature)
            args.putString(ARG_DEFAULT_NAME, defaultName)
            fragment.arguments = args
            return fragment
        }
    }
}
