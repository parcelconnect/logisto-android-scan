package com.tubbert.powdroid.ui

import android.support.annotation.ColorRes
import android.view.View

/**
 *
 */
object ResourceHelper {

    public fun View.setBackgroundColorRes(@ColorRes colorRes: Int) {
        setBackgroundColor(
                resources.getColor(colorRes))
    }

}