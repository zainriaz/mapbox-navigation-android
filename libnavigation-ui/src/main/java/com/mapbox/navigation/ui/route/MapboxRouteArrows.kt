package com.mapbox.navigation.ui.route

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.Property.NONE
import com.mapbox.mapboxsdk.style.layers.Property.VISIBLE
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.MathUtils
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.internal.route.RouteConstants
import com.mapbox.navigation.ui.internal.route.RouteConstants.MAX_DEGREES
import com.mapbox.navigation.ui.internal.utils.MapImageUtils
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.mapbox.turf.TurfMisc
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A more featured implementation for adding multiple route arrows on the map. While this class
 * will add a maneuver arrow based on [RouteProgress] you can also add additional arrows based
 * on a collection of two or more points. It is suggested that you use either this class
 * for managing route arrows or the embedded default implementation that is created and managed by
 * [NavigationMapRoute]. To use this class exclusively you should call
 * [NavigationMapRoute.updateRouteArrowVisibilityTo(false)] in order to disable the default
 * implementation. You should also register your own [RouteProgressObserver] and pass the
 * [RouteProgress] objects emitted to this class if you want the same maneuver arrow behavior as
 * the default implementation. For more fine grained control you can add and remove arrows
 * at any time using the methods in this class.
 */
class MapboxRouteArrows(
    private val options: RouteArrowOptions
) {

    companion object {
        const val ARROW_BEARING_ADVANCED = "mapbox-navigation-arrow-bearing-advanced"
        const val ARROW_SHAFT_SOURCE_ID_ADVANCED = "mapbox-navigation-arrow-shaft-source-advanced"
        const val ARROW_HEAD_SOURCE_ID_ADVANCED = "mapbox-navigation-arrow-head-source-advanced"
        const val ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED =
            "mapbox-navigation-arrow-shaft-casing-layer-advanced"
        const val ARROW_SHAFT_LINE_LAYER_ID_ADVANCED = "mapbox-navigation-arrow-shaft-layer-advanced"
        const val ARROW_HEAD_ICON_ADVANCED = "mapbox-navigation-arrow-head-icon-advanced"
        const val ARROW_HEAD_ICON_CASING_ADVANCED = "mapbox-navigation-arrow-head-icon-casing-advanced"
        const val ARROW_HEAD_CASING_LAYER_ID_ADVANCED = "mapbox-navigation-arrow-head-casing-layer-advanced"
        const val ARROW_HEAD_LAYER_ID_ADVANCED = "mapbox-navigation-arrow-head-layer-advanced"
    }

    private val arrows: CopyOnWriteArrayList<List<Point>> = CopyOnWriteArrayList()
    private var maneuverArrow: List<Point> = listOf()

    /**
     * Returns all of the collections of points making up arrows that have been added. Each
     * collection of points represents a single arrow.
     */
    fun getArrows(): List<List<Point>> {
        return arrows.toList()
    }

    /**
     * Adds an arrow representing the next maneuver. There is at most one maneuver arrow on the
     * map at any given time. For each [RouteProgress] submitted the next maneuver is calculated.
     * If the newly calculated maneuver arrow is different from the current maneuver arrow, the
     * existing maneuver arrow is replaced with the newly calculated arrow. Other arrows added
     * via the [addArrow] call are not affected when calling this method.
     *
     * @param style a valid map style
     * @param routeProgress a route progress object used for the maneuver arrow calculation.
     */
    fun addUpcomingManeuverArrow(style: Style, routeProgress: RouteProgress) {
        val invalidUpcomingStepPoints = (routeProgress.upcomingStepPoints == null
            || routeProgress.upcomingStepPoints!!.size < RouteConstants.TWO_POINTS)
        val invalidCurrentStepPoints = routeProgress.currentLegProgress == null ||
                routeProgress.currentLegProgress!!.currentStepProgress == null ||
                routeProgress.currentLegProgress!!.currentStepProgress!!.stepPoints == null ||
                routeProgress.currentLegProgress!!.currentStepProgress!!.stepPoints!!.size <
                    RouteConstants.TWO_POINTS

        if (invalidUpcomingStepPoints || invalidCurrentStepPoints) {
            return
        }

        removeArrow(style, maneuverArrow)
        maneuverArrow = obtainArrowPointsFrom(routeProgress)
        addArrow(style, maneuverArrow)
    }

    /**
     * Adds an arrow to the map made up of the points submitted. An arrow is made up of at least
     * two points. The direction of the arrow head is determined by calculating the bearing
     * between the last two points submitted. Each call will add a new arrow.
     *
     * @param style a valid map style
     * @param points the points that make up the arrow to be drawn
     */
    fun addArrow(style: Style, points: List<Point>) {
        if (points.size < RouteConstants.TWO_POINTS || !style.isFullyLoaded) {
            return
        }

        initializeLayers(style)

        if (arrows.flatten().intersect(points).isEmpty()) {
            arrows.add(points)
            redrawArrows(style)
        }
    }

    /**
     * Will remove any arrow having one or more points contained in the points submitted.
     * To remove a previously added arrow it isn't necessary to submit all of the points of the
     * previously submitted arrow(s). Instead it is necessary only to submit at least one point
     * for each previously added arrow that should be removed.
     *
     * @param style a valid map style
     * @param points one or more points used as criteria for removing arrows from the map
     */
    fun removeArrow(style: Style, points: List<Point>) {
        initializeLayers(style)

        val arrowsToRemove = arrows.filter { it.intersect(points).isNotEmpty() }
        if (maneuverArrow.intersect(points).isNotEmpty()) {
            maneuverArrow = listOf()
        }
        arrows.removeAll(arrowsToRemove)
        redrawArrows(style)
    }

    /**
     * Clears all arrows from the map.
     *
     * @param style a valid map style
     */
    fun clearArrows(style: Style) {
        initializeLayers(style)
        arrows.clear()
        maneuverArrow = listOf()
        redrawArrows(style)
    }

    /**
     * Returns a value indicating whether or not the map layers that host the arrows are visible.
     *
     * @param style a valid map style
     */
    fun arrowsAreVisible(style: Style): Boolean {
        return style.getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED)?.visibility?.value ?: "" ==
            VISIBLE &&
        style.getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED)?.visibility?.value ?: "" == VISIBLE &&
        style.getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED)?.visibility?.value ?: "" == VISIBLE &&
        style.getLayer(ARROW_HEAD_LAYER_ID_ADVANCED)?.visibility?.value ?: "" == VISIBLE
    }

    /**
     * Sets the layers hosting the arrows to visible.
     *
     * @param style a valid map style
     */
    fun show(style: Style) {
        setVisibility(style, VISIBLE)
    }

    /**
     * Hides the layers hosting the arrows.
     *
     * @param style a valid map style
     */
    fun hide(style: Style) {
        setVisibility(style, NONE)
    }

    private fun redrawArrows(style: Style) {
        val shaftFeatures = arrows.map {
            LineString.fromLngLats(it)
        }.map {
            Feature.fromGeometry(it)
        }
        val shaftFeatureCollection = FeatureCollection.fromFeatures(shaftFeatures)

        val arrowHeadFeatures = arrows.map {
            val azimuth = TurfMeasurement.bearing(it[it.size - 2], it[it.size - 1])
            Feature.fromGeometry(it[it.size - 1]).also {  feature ->
                feature.addNumberProperty(
                    ARROW_BEARING_ADVANCED,
                    MathUtils.wrap(azimuth, 0.0, MAX_DEGREES.toDouble())
                )
            }
        }
        val arrowHeadFeatureCollection = FeatureCollection.fromFeatures(arrowHeadFeatures)

        style.getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED)?.setGeoJson(shaftFeatureCollection)
        style.getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED)?.setGeoJson(arrowHeadFeatureCollection)
    }

    private fun initializeLayers(style: Style) {
        if (!style.isFullyLoaded || layersAreInitialized(style)) {
            return
        }

        initializeArrowShaft(style, options.sourceMaxZoom, options.sourceTolerance)
        initializeArrowHead(style, options.sourceMaxZoom, options.sourceTolerance)
        addArrowHeadIcon(style, options.arrowHeadIcon, options.arrowColor)
        addArrowHeadIconCasing(style, options.arrowHeadIconBorder, options.arrowBorderColor)

        val shaftLayer = createArrowShaftLayer(style, options.arrowColor)
        val shaftCasingLayer = createArrowShaftCasingLayer(style, options.arrowBorderColor)
        val headLayer = createArrowHeadLayer(style)
        val headCasingLayer = createArrowHeadCasingLayer(style)

        style.addLayerAbove(shaftCasingLayer, options.aboveLayerId)
        style.addLayerAbove(headCasingLayer, shaftCasingLayer.id)
        style.addLayerAbove(shaftLayer, headCasingLayer.id)
        style.addLayerAbove(headLayer, shaftLayer.id)


    }

    private fun layersAreInitialized(style: Style): Boolean {
        return style.isFullyLoaded &&
            style.getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) != null &&
            style.getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) != null &&
            style.getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) != null &&
            style.getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) != null &&
            style.getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) != null &&
            style.getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) != null &&
            style.getImage(ARROW_HEAD_ICON_ADVANCED) != null &&
            style.getImage(ARROW_HEAD_ICON_CASING_ADVANCED) != null
    }

    private fun initializeArrowShaft(style: Style, sourceMaxZoom: Int, sourceTolerance: Float) {
        if (style.getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) != null) {
            return
        }

        val geoJsonOptions = GeoJsonOptions()
        geoJsonOptions.withMaxZoom(sourceMaxZoom)
        geoJsonOptions.withTolerance(sourceTolerance)
        GeoJsonSource(
            ARROW_SHAFT_SOURCE_ID_ADVANCED,
            FeatureCollection.fromFeatures(listOf()),
            geoJsonOptions
        ).let {
            style.addSource(it)
        }
    }

    private fun initializeArrowHead(style: Style, sourceMaxZoom: Int, sourceTolerance: Float) {
        if (style.getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) != null) {
            return
        }

        val geoJsonOptions = GeoJsonOptions()
        geoJsonOptions.withMaxZoom(sourceMaxZoom)
        geoJsonOptions.withTolerance(sourceTolerance)
        GeoJsonSource(
            ARROW_HEAD_SOURCE_ID_ADVANCED,
            FeatureCollection.fromFeatures(listOf()),
            geoJsonOptions
        ).let {
            style.addSource(it)
        }
    }

    private fun addArrowHeadIcon(
        style: Style,
        arrowHeadIcon: Drawable,
        arrowColor: Int
    ) {
        if (style.getImage(ARROW_HEAD_ICON_ADVANCED) != null) {
            return
        }

        val head = DrawableCompat.wrap(arrowHeadIcon)
        DrawableCompat.setTint(head.mutate(), arrowColor)
        val icon = MapImageUtils.getBitmapFromDrawable(head)
        style.addImage(ARROW_HEAD_ICON_ADVANCED, icon)
    }

    private fun addArrowHeadIconCasing(
        style: Style,
        arrowHeadCasing: Drawable,
        arrowBorderColor: Int
    ) {
        if (style.getImage(ARROW_HEAD_ICON_CASING_ADVANCED) != null) {
            return
        }

        val headCasing = DrawableCompat.wrap(arrowHeadCasing)
        DrawableCompat.setTint(headCasing.mutate(), arrowBorderColor)
        val icon = MapImageUtils.getBitmapFromDrawable(headCasing)
        style.addImage(ARROW_HEAD_ICON_CASING_ADVANCED, icon)
    }

    private fun createArrowShaftLayer(style: Style, arrowColor: Int): LineLayer {
        val shaftLayer = style.getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED)?.run {
                this as LineLayer
        }
        if (shaftLayer != null) {
            style.removeLayer(shaftLayer)
        }
        return LineLayer(
            ARROW_SHAFT_LINE_LAYER_ID_ADVANCED,
            ARROW_SHAFT_SOURCE_ID_ADVANCED
        ).withProperties(
            PropertyFactory.lineColor(Expression.color(arrowColor)),
            PropertyFactory.lineWidth(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(
                        RouteConstants.MIN_ARROW_ZOOM,
                        RouteConstants.MIN_ZOOM_ARROW_SHAFT_SCALE
                    ),
                    Expression.stop(
                        RouteConstants.MAX_ARROW_ZOOM,
                        RouteConstants.MAX_ZOOM_ARROW_SHAFT_SCALE
                    )
                )
            ),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
            PropertyFactory.lineOpacity(
                Expression.step(
                    Expression.zoom(), RouteConstants.OPAQUE,
                    Expression.stop(
                        RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
                    )
                )
            )
        )
    }

    private fun createArrowShaftCasingLayer(
        style: Style,
        arrowBorderColor: Int
    ): LineLayer {
        val shaftCasingLayer = style.getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED)?.run {
            this as LineLayer
        }
        if (shaftCasingLayer != null) {
            style.removeLayer(shaftCasingLayer)
        }
        return LineLayer(
            ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED,
            ARROW_SHAFT_SOURCE_ID_ADVANCED
        ).withProperties(
            PropertyFactory.lineColor(Expression.color(arrowBorderColor)),
            PropertyFactory.lineWidth(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(
                        RouteConstants.MIN_ARROW_ZOOM,
                        RouteConstants.MIN_ZOOM_ARROW_SHAFT_CASING_SCALE
                    ),
                    Expression.stop(
                        RouteConstants.MAX_ARROW_ZOOM,
                        RouteConstants.MAX_ZOOM_ARROW_SHAFT_CASING_SCALE
                    )
                )
            ),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
            PropertyFactory.lineOpacity(
                Expression.step(
                    Expression.zoom(), RouteConstants.OPAQUE,
                    Expression.stop(
                        RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
                    )
                )
            )
        )
    }

    private fun createArrowHeadLayer(style: Style): SymbolLayer {
        val headLayer = style.getLayer(ARROW_HEAD_LAYER_ID_ADVANCED)?.run {
            this as SymbolLayer
        }
        if (headLayer != null) {
            style.removeLayer(headLayer)
        }
        return SymbolLayer(ARROW_HEAD_LAYER_ID_ADVANCED, ARROW_HEAD_SOURCE_ID_ADVANCED)
            .withProperties(
                PropertyFactory.iconImage(ARROW_HEAD_ICON_ADVANCED),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconSize(
                    Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.stop(
                            RouteConstants.MIN_ARROW_ZOOM,
                            RouteConstants.MIN_ZOOM_ARROW_HEAD_SCALE
                        ),
                        Expression.stop(
                            RouteConstants.MAX_ARROW_ZOOM,
                            RouteConstants.MAX_ZOOM_ARROW_HEAD_SCALE
                        )
                    )
                ),
                PropertyFactory.iconOffset(RouteConstants.ARROW_HEAD_OFFSET),
                PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),
                PropertyFactory.iconRotate(Expression.get(ARROW_BEARING_ADVANCED)),
                PropertyFactory.iconOpacity(
                    Expression.step(
                        Expression.zoom(), RouteConstants.OPAQUE,
                        Expression.stop(
                            RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
                        )
                    )
                )
            )
    }

    private fun createArrowHeadCasingLayer(style: Style): SymbolLayer {
        val headCasingLayer = style.getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED)?.run {
            this as SymbolLayer
        }
        if (headCasingLayer != null) {
            style.removeLayer(headCasingLayer)
        }
        return SymbolLayer(
            ARROW_HEAD_CASING_LAYER_ID_ADVANCED,
            ARROW_HEAD_SOURCE_ID_ADVANCED
        ).withProperties(
            PropertyFactory.iconImage(ARROW_HEAD_ICON_CASING_ADVANCED),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true),
            PropertyFactory.iconSize(
                Expression.interpolate(
                    Expression.linear(), Expression.zoom(),
                    Expression.stop(
                        RouteConstants.MIN_ARROW_ZOOM,
                        RouteConstants.MIN_ZOOM_ARROW_HEAD_CASING_SCALE
                    ),
                    Expression.stop(
                        RouteConstants.MAX_ARROW_ZOOM,
                        RouteConstants.MAX_ZOOM_ARROW_HEAD_CASING_SCALE
                    )
                )
            ),
            PropertyFactory.iconOffset(RouteConstants.ARROW_HEAD_CASING_OFFSET),
            PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),
            PropertyFactory.iconRotate(Expression.get(ARROW_BEARING_ADVANCED)),
            PropertyFactory.iconOpacity(
                Expression.step(
                    Expression.zoom(), RouteConstants.OPAQUE,
                    Expression.stop(
                        RouteConstants.ARROW_HIDDEN_ZOOM_LEVEL, RouteConstants.TRANSPARENT
                    )
                )
            )
        )
    }

    private fun setVisibility(style: Style, visibilityValue: String) {
        style.getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED)
            ?.setProperties(PropertyFactory.visibility(visibilityValue))

        style.getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED)
            ?.setProperties(PropertyFactory.visibility(visibilityValue))

        style.getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED)
            ?.setProperties(PropertyFactory.visibility(visibilityValue))

        style.getLayer(ARROW_HEAD_LAYER_ID_ADVANCED)
            ?.setProperties(PropertyFactory.visibility(visibilityValue))
    }

    internal fun obtainArrowPointsFrom(routeProgress: RouteProgress): List<Point> {
        val reversedCurrent = routeProgress.currentLegProgress
            ?.currentStepProgress
            ?.stepPoints
            ?.reversed() ?: listOf()

        val arrowLineCurrent = LineString.fromLngLats(reversedCurrent)
        val arrowLineUpcoming = LineString.fromLngLats(routeProgress.upcomingStepPoints!!)
        val arrowCurrentSliced = TurfMisc.lineSliceAlong(
            arrowLineCurrent,
            0.0,
            RouteConstants.THIRTY.toDouble(),
            TurfConstants.UNIT_METERS
        )
        val arrowUpcomingSliced = TurfMisc.lineSliceAlong(
            arrowLineUpcoming,
            0.0,
            RouteConstants.THIRTY.toDouble(),
            TurfConstants.UNIT_METERS
        )

        return arrowCurrentSliced.coordinates().reversed().plus(arrowUpcomingSliced.coordinates())
    }
}
