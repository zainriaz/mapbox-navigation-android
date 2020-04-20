package com.mapbox.navigation.examples.ui.exp

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.mapbox.navigation.examples.R

val resumeExampleViewUpdateCmd: ViewEffect<String, Activity> = { input -> { activity ->
    activity.window.findViewById<View>(R.id.saveMinutes)?.let {
        if(it is TextView) {
            it.text = input
        }
    }
}  }