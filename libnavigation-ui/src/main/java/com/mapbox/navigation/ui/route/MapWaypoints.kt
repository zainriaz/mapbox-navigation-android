package com.mapbox.navigation.ui.route

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteLeg
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.libnavigation.ui.R
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.navigation.ui.utils.MapImageUtils
import com.mapbox.navigation.ui.utils.MapUtils

internal class MapWaypoints(
    private val style: Style
) {
    private var waypointFeatureCollection: FeatureCollection = FeatureCollection.fromFeatures(arrayOf())

    val waypointSource: GeoJsonSource by lazy {
        val wayPointGeoJsonOptions = GeoJsonOptions().withMaxZoom(16)
        val wayPointSource = GeoJsonSource(
            RouteConstants.WAYPOINT_SOURCE_ID,
            waypointFeatureCollection,
            wayPointGeoJsonOptions
        )
        style.addSource(wayPointSource)
        return@lazy wayPointSource
    }

    fun initializeLayers(context: Context, styleRes: Int, belowLayerId: String): String {
        val originWaypointIcon = MapRouteLine.MapRouteLineSupport.getResourceStyledValue(
            R.styleable.NavigationMapRoute_originWaypointIcon,
            R.drawable.ic_route_origin,
            context,
            styleRes
        )

        val destinationWaypointIcon = MapRouteLine.MapRouteLineSupport.getResourceStyledValue(
            R.styleable.NavigationMapRoute_destinationWaypointIcon,
            R.drawable.ic_route_destination,
            context,
            styleRes
        )
        val originIcon = AppCompatResources.getDrawable(context, originWaypointIcon)
        val destinationIcon = AppCompatResources.getDrawable(context, destinationWaypointIcon)

        val symbolLayer = initializeWayPointLayer(style, originIcon, destinationIcon)
        MapUtils.addLayerToMap(style, symbolLayer, belowLayerId)
        return symbolLayer.id
    }

    fun drawWayPoints(directionsRoute: DirectionsRoute?) {
        directionsRoute?.let {
            setWaypointsSource(buildWayPointFeatureCollection(directionsRoute))
        }
    }

    fun clearRouteData() {
        setWaypointsSource(FeatureCollection.fromFeatures(arrayOf()))
    }

    private fun initializeWayPointLayer(style: Style, originIcon: Drawable?,
                                destinationIcon: Drawable?): SymbolLayer {
        var wayPointLayer = style.getLayerAs<SymbolLayer>(RouteConstants.WAYPOINT_LAYER_ID)
        if (wayPointLayer != null) {
            style.removeLayer(wayPointLayer)
        }
        var bitmap = MapImageUtils.getBitmapFromDrawable(originIcon)
        style.addImage(RouteConstants.ORIGIN_MARKER_NAME, bitmap!!)
        bitmap = MapImageUtils.getBitmapFromDrawable(destinationIcon)
        style.addImage(RouteConstants.DESTINATION_MARKER_NAME, bitmap)
        wayPointLayer = SymbolLayer(RouteConstants.WAYPOINT_LAYER_ID, RouteConstants.WAYPOINT_SOURCE_ID).withProperties(
            PropertyFactory.iconImage(
                Expression.match(
                    Expression.toString(Expression.get(RouteConstants.WAYPOINT_PROPERTY_KEY)), Expression.literal(RouteConstants.ORIGIN_MARKER_NAME),
                    Expression.stop(RouteConstants.WAYPOINT_ORIGIN_VALUE, Expression.literal(RouteConstants.ORIGIN_MARKER_NAME)),
                    Expression.stop(RouteConstants.WAYPOINT_DESTINATION_VALUE, Expression.literal(RouteConstants.DESTINATION_MARKER_NAME))
                )),
            PropertyFactory.iconSize(
                Expression.interpolate(
                    Expression.exponential(1.5f), Expression.zoom(),
                    Expression.stop(0f, 0.6f),
                    Expression.stop(10f, 0.8f),
                    Expression.stop(12f, 1.3f),
                    Expression.stop(22f, 2.8f)
                )
            ),
            PropertyFactory.iconPitchAlignment(Property.ICON_PITCH_ALIGNMENT_MAP),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )
        return wayPointLayer
    }

    private fun buildWayPointFeatureCollection(route: DirectionsRoute): FeatureCollection {
        val wayPointFeatures = mutableListOf<Feature>()
        route.legs()?.forEach {
            buildWayPointFeatureFromLeg(it, 0)?.let { feature ->
                wayPointFeatures.add(feature)
            }

            it.steps()?.let { steps ->
                buildWayPointFeatureFromLeg(it, steps.lastIndex)?.let { feature ->
                    wayPointFeatures.add(feature)
                }
            }
        }
        return FeatureCollection.fromFeatures(wayPointFeatures)
    }

    private fun buildWayPointFeatureFromLeg(leg: RouteLeg, index: Int): Feature? {
        return leg.steps()?.get(index)?.maneuver()?.location()?.run {
            Feature.fromGeometry(Point.fromLngLat(this.longitude(), this.latitude()))
        }?.also { feature ->
            val propValue = if (index == 0) {
                RouteConstants.WAYPOINT_ORIGIN_VALUE
            } else {
                RouteConstants.WAYPOINT_DESTINATION_VALUE
            }
            feature.addStringProperty(RouteConstants.WAYPOINT_PROPERTY_KEY, propValue)
        }
    }

    private fun setWaypointsSource(featureCollection: FeatureCollection) {
        waypointFeatureCollection = featureCollection
        waypointSource.setGeoJson(waypointFeatureCollection)
    }
}
