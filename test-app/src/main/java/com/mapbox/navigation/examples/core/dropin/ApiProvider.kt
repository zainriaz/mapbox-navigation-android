package com.mapbox.navigation.examples.core.dropin

import com.mapbox.navigation.ui.base.MapboxView
import com.mapbox.navigation.ui.base.api.tripprogress.TripProgressApi
import com.mapbox.navigation.ui.base.model.tripprogress.TripProgressState

class ApiProvider(
    private val tripProgress: Pair<TripProgressApi, MapboxView<TripProgressState>>?
) {

    // todo needs more work

    fun getTripProgressApi(): Pair<TripProgressApi, MapboxView<TripProgressState>>? = tripProgress

    class Builder {
        private var tripProgress: Pair<TripProgressApi, MapboxView<TripProgressState>>? = null

        fun withTripProgress(api: TripProgressApi, view:  MapboxView<TripProgressState>): Builder =
            apply { this.tripProgress = Pair(api, view) }

        fun build(): ApiProvider {
            return ApiProvider(tripProgress)
        }
    }
}
