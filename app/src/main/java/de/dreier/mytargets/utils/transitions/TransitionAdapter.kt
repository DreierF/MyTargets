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

package de.dreier.mytargets.utils.transitions

import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.Transition

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
abstract class TransitionAdapter : Transition.TransitionListener {
    override fun onTransitionStart(transition: Transition) {

    }

    override fun onTransitionCancel(transition: Transition) {

    }

    override fun onTransitionPause(transition: Transition) {

    }

    override fun onTransitionResume(transition: Transition) {

    }
}
