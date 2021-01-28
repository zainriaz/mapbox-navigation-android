package com.mapbox.navigation.ui.base.api

import com.mapbox.navigation.ui.base.MapboxState

abstract class MapboxApi<S: MapboxState, O: MapboxStateObserver<S>>(initialState: S): Api<S, O> {
    private var observer: O? = null
    var state: S = initialState
        protected set(value) {
            field = value
            observer?.onStateChanged(state)
        }

    override fun observeState(stateObserver: O) {
        observer = stateObserver
        observer?.onStateChanged(state)
    }
}

interface Api<S: MapboxState, O: MapboxStateObserver<S>> {
    fun observeState(stateObserver: O)
}
