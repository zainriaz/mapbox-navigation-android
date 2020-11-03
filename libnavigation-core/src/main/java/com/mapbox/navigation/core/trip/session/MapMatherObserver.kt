package com.mapbox.navigation.core.trip.session

interface MapMatcherResultObserver {
    fun onNewMapMatcherResult(mapMatcherResult: MapMatcherResult)
}
