package com.mapbox.navigation.ui.maps.route.line.model

/**
 * This class is used for describing the route line color(s) at runtime.
 *
 * @param routeIdentifier a string that identifies routes which should have their color overridden
 * @param lineColorResourceId the color of the route line
 * @param lineCasingColorResourceId the color of the shield line which appears below the route line
 * and is normally wider providing a visual border for the route line.
 */
data class RouteStyleDescriptor(
    val routeIdentifier: String,
    val lineColorResourceId: Int,
    val lineCasingColorResourceId: Int
)
