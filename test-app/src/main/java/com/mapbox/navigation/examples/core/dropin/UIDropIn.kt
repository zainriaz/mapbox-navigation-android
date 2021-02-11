package com.mapbox.navigation.examples.core.dropin

import android.os.Bundle
import com.mapbox.api.directions.v5.models.DirectionsRoute

interface UIDropIn {

    fun saveStateWith(key: String, outState: Bundle)
    fun drawRoute(route: DirectionsRoute)
    fun startNavigation()
}
