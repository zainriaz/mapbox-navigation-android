package com.mapbox.navigation.ui.base.internal.utils

import android.view.View
import java.util.HashSet

class MultiOnClickListener : View.OnClickListener {

    private val onClickListeners: MutableSet<View.OnClickListener> = HashSet()

    fun addListener(onClickListener: View.OnClickListener) {
        onClickListeners.add(onClickListener)
    }

    fun removeListener(onClickListener: View.OnClickListener) {
        onClickListeners.remove(onClickListener)
    }

    fun clearListeners() {
        onClickListeners.clear()
    }

    override fun onClick(view: View) {
        for (onClickListener in onClickListeners) {
            onClickListener.onClick(view)
        }
    }
}
