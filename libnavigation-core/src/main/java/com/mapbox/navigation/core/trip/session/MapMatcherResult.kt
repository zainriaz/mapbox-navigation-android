package com.mapbox.navigation.core.trip.session

import android.location.Location

class MapMatcherResult internal constructor(
    val enhancedLocation: Location,
    val keyPoints: List<Location>,
    val isOffRoad: Boolean,
    val offRoadProbability: Float,
    val isTeleport: Boolean,
    val roadEdgeMatchProbability: Float
) {
    // todo equals/hash
}
