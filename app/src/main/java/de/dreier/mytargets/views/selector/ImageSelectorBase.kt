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

package de.dreier.mytargets.views.selector

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import de.dreier.mytargets.R

abstract class ImageSelectorBase<T> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet?,
        requestCode: Int
) : SelectorBase<T>(context, attrs, R.layout.selector_item_image_details, requestCode) where T : Parcelable
