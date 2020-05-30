package com.mapbox.navigation.core.replay.route

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import java.util.Collections
import kotlin.math.abs
import kotlin.math.min

internal class ReplayRouteBender {

    companion object {
        const val MAX_BEARING_DELTA = 40.0
        const val MAX_CURVE_RADIUS_METERS = 5.0
    }

    fun maxBearingsDelta(points: List<Point>): Double {
        var maxBearingDelta = 0.0
        var previousBearing = TurfMeasurement.bearing(points[0], points[1])
        for (i in 1..points.lastIndex) {
            val bearing = TurfMeasurement.bearing(points[i - 1], points[i])
            val delta = abs(bearing-previousBearing)
            maxBearingDelta = if (delta > maxBearingDelta) delta else maxBearingDelta
            previousBearing = bearing
        }
        return maxBearingDelta
    }

    fun bendRoute(
        route: List<Point>
    ): List<Point> {

        println("bendRoute request ${LineString.fromLngLats(route).toJson()}")

        val curveCount = route.size - 2
        val pointCurves = Array(curveCount) {
            mutableListOf<Point>()
        }

        for (i in 1 until route.lastIndex) {
            val pointCurve = locationCurve(
                route[i - 1],
                route[i],
                route[i + 1]
            )
            pointCurves[i - 1].addAll(pointCurve)
        }

        val bentRoute = mutableListOf<Point>()
        bentRoute.add(route.first())
        pointCurves.forEach { it.forEach { item -> bentRoute.add(item) } }
        bentRoute.add(route.last())

        println("bendRoute results ${LineString.fromLngLats(bentRoute).toJson()}")

        return bentRoute
    }

    private fun locationCurve(
        start: Point,
        mid: Point,
        end: Point
    ): List<Point> {
        val startBearing = TurfMeasurement.bearing(start, mid)
        val endBearing = TurfMeasurement.bearing(mid, end)
        val deltaBearing = abs(endBearing - startBearing)
        if (deltaBearing < MAX_BEARING_DELTA) {
            return Collections.singletonList(mid)
        }

        val startDistance = TurfMeasurement.distance(start, mid, TurfConstants.UNIT_METERS)
        val endDistance = TurfMeasurement.distance(mid, end, TurfConstants.UNIT_METERS)

        val startRadius = min(startDistance - 0.1, MAX_CURVE_RADIUS_METERS)
        val startMultiplier = 1.0 - (startRadius / startDistance)
        val startPoint = pointAlong(start, mid, startMultiplier * startDistance)

        val endRadius = min(endDistance - 0.1, MAX_CURVE_RADIUS_METERS)
        val endMultiplier = (endRadius / endDistance)
        val endPoint = pointAlong(mid, end, endMultiplier * endDistance)

        println("locationCurve bearings [$startBearing $endBearing $deltaBearing] distances [$startDistance $endDistance] multipliers [$startMultiplier $endMultiplier]")
        val curve =  curve(startPoint, mid, endPoint)

        println("curved: ${LineString.fromLngLats(curve).toJson()}")
        return curve
    }

    private fun curve(
        startPoint: Point,
        mid: Point,
        endPoint: Point
    ): List<Point> {
        val startToMidDistance = TurfMeasurement.distance(
            startPoint,
            mid,
            TurfConstants.UNIT_METERS
        )
        val midToEndDistance = TurfMeasurement.distance(
            mid,
            endPoint,
            TurfConstants.UNIT_METERS
        )

        val granularity = 4
        val curvePoints = mutableListOf<Point>()
        for (i in 0..granularity) {
            val lerpMultiplier = (i.toDouble() / granularity)
            val fromLerpPoint = pointAlong(
                startPoint,
                mid,
                lerpMultiplier * startToMidDistance
            )
            val toLerpPoint = pointAlong(mid, endPoint, lerpMultiplier * midToEndDistance)
            val lerpDistance = TurfMeasurement.distance(
                fromLerpPoint,
                toLerpPoint,
                TurfConstants.UNIT_METERS
            )
            val curvePoint = pointAlong(fromLerpPoint, toLerpPoint, lerpMultiplier * lerpDistance)
            curvePoints.add(curvePoint)
        }

        return curvePoints
    }

    private fun pointAlong(start: Point, end: Point, distance: Double): Point {
        val direction = TurfMeasurement.bearing(end, start) - 180.0
        return TurfMeasurement.destination(start, distance, direction, TurfConstants.UNIT_METERS)
    }
}
