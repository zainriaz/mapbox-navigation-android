package com.mapbox.navigation.ui.base.api

import com.mapbox.navigation.ui.base.MapboxState

interface MapboxStateObserver<T: MapboxState> {
    fun onStateChanged(newState: T)
}
