package com.mapbox.navigation.core.trip.session

import android.location.Location

class MapMatcherResult(
    val enhancedLocation: Location,
    val isOffRoad: Boolean,
    val offRoadProbability: Float,
    val isTeleport: Boolean,
    val roadEdgeMatchProbability: Float
) {
    // todo equals/hash
}
