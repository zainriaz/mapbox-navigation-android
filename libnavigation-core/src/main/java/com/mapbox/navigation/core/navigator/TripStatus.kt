package com.mapbox.navigation.core.navigator

import android.location.Location
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.trip.session.MapMatcherResult

/**
 * State of a trip at a particular timestamp.
 *
 * @param enhancedLocation the user's location
 * @param keyPoints list of predicted locations. Might be empty.
 * @param routeProgress [RouteProgress] is progress information
 * @param offRoute *true* if user is off-route, *false* otherwise
 * @param mapMatcherResult map-matcher data from [NavigationStatus]
 */
internal data class TripStatus(
    val enhancedLocation: Location,
    val keyPoints: List<Location>,
    val routeProgress: RouteProgress?,
    val offRoute: Boolean,
    val mapMatcherResult: MapMatcherResult
)
