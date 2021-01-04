package com.mapbox.navigation.ui.route

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property.NONE
import com.mapbox.mapboxsdk.style.layers.Property.VISIBLE
import com.mapbox.mapboxsdk.style.layers.PropertyValue
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.testing.FileUtils.loadJsonFixture
import com.mapbox.navigation.ui.R
import com.mapbox.navigation.ui.internal.ThemeSwitcher
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_HEAD_CASING_LAYER_ID_ADVANCED
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_HEAD_ICON_ADVANCED
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_HEAD_ICON_CASING_ADVANCED
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_HEAD_LAYER_ID_ADVANCED
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_HEAD_SOURCE_ID_ADVANCED
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_SHAFT_LINE_LAYER_ID_ADVANCED
import com.mapbox.navigation.ui.route.MapboxRouteArrows.Companion.ARROW_SHAFT_SOURCE_ID_ADVANCED
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapboxRouteArrowsTest {

    lateinit var ctx: Context
    var styleRes: Int = 0

    @Before
    fun setUp() {
        ctx = ApplicationProvider.getApplicationContext()
        styleRes = ThemeSwitcher.retrieveAttrResourceId(
            ctx,
            R.attr.navigationViewRouteStyle,
            R.style.MapboxStyleNavigationMapRoute
        )
    }

    @Test
    fun addArrow_doesNotAddDuplicates() {
        val mockBitMap = mockk<Bitmap>()
        val mockSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val mockLayer = mockk<Layer>()
        val style = mockk<Style>(relaxUnitFun = true) {
            every { isFullyLoaded } returns true
            every { getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns mockLayer
            every { getImage(ARROW_HEAD_ICON_ADVANCED) } returns mockBitMap
            every { getImage(ARROW_HEAD_ICON_CASING_ADVANCED) } returns mockBitMap
        }
        val firstPoints = listOf(
            Point.fromLngLat(-122.528540, 37.971168),
            Point.fromLngLat(-122.528637, 37.970187),
            Point.fromLngLat(-122.528076, 37.969760)
        )

        val secondPoints = listOf(
            Point.fromLngLat(-122.528076, 37.969760),
            Point.fromLngLat(-122.527418, 37.969325),
            Point.fromLngLat(-122.526409, 37.968767)
        )

        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())
        arrows.addArrow(style, firstPoints)
        arrows.addArrow(style, secondPoints)

        assertEquals(1, arrows.getArrows().size)
    }

    @Test
    fun addArrow() {
        val expectedShaftSlot = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":" +
            "\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":" +
            "[[-122.52854,37.971168],[-122.528637,37.970187],[-122.528076,37.96976]]}," +
            "\"properties\":{}}]}"
        val expectedHeadSlot = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":" +
            "\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":" +
            "[-122.528076,37.96976]},\"properties\":" +
            "{\"mapbox-navigation-arrow-bearing-advanced\":133.99441931464878}}]}"
        val shaftSlot = slot<FeatureCollection>()
        val headSlot = slot<FeatureCollection>()
        val mockBitMap = mockk<Bitmap>()
        val shaftSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val headSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val mockLayer = mockk<Layer>()
        val style = mockk<Style>(relaxUnitFun = true) {
            every { isFullyLoaded } returns true
            every { getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns shaftSource
            every { getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns headSource
            every { getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns shaftSource
            every { getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns headSource
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns mockLayer
            every { getImage(ARROW_HEAD_ICON_ADVANCED) } returns mockBitMap
            every { getImage(ARROW_HEAD_ICON_CASING_ADVANCED) } returns mockBitMap
        }
        val firstPoints = listOf(
            Point.fromLngLat(-122.528540, 37.971168),
            Point.fromLngLat(-122.528637, 37.970187),
            Point.fromLngLat(-122.528076, 37.969760)
        )

        val secondPoints = listOf(
            Point.fromLngLat(-122.528076, 37.969760),
            Point.fromLngLat(-122.527418, 37.969325),
            Point.fromLngLat(-122.526409, 37.968767)
        )

        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())
        arrows.addArrow(style, firstPoints)
        arrows.addArrow(style, secondPoints)

        verify { shaftSource.setGeoJson(capture(shaftSlot)) }
        verify { headSource.setGeoJson(capture(headSlot)) }
        assertEquals(expectedShaftSlot, shaftSlot.captured.toJson())
        assertEquals(expectedHeadSlot, headSlot.captured.toJson())
    }

    @Test
    fun removeArrow() {
        val mockBitMap = mockk<Bitmap>()
        val mockSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val mockLayer = mockk<Layer>()
        val style = mockk<Style>(relaxUnitFun = true) {
            every { isFullyLoaded } returns true
            every { getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns mockLayer
            every { getImage(ARROW_HEAD_ICON_ADVANCED) } returns mockBitMap
            every { getImage(ARROW_HEAD_ICON_CASING_ADVANCED) } returns mockBitMap
        }
        val firstPoints = listOf(
            Point.fromLngLat(-122.528540, 37.971168),
            Point.fromLngLat(-122.528637, 37.970187),
            Point.fromLngLat(-122.528076, 37.969760)
        )
        val secondPoints = listOf(
            Point.fromLngLat(-122.527418, 37.969325),
            Point.fromLngLat(-122.526409, 37.968767)
        )
        val arrowToRemove = listOf(
            Point.fromLngLat(-122.526409, 37.968767),
            Point.fromLngLat(-122.525433, 37.968209, )
        )
        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build()).also {
            it.addArrow(style, firstPoints)
            it.addArrow(style, secondPoints)
        }
        assertEquals(2, arrows.getArrows().size)

        arrows.removeArrow(style, arrowToRemove)

        assertEquals(1, arrows.getArrows().size)
    }

    @Test
    fun clearArrows() {
        val mockBitMap = mockk<Bitmap>()
        val mockSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val mockLayer = mockk<Layer>()
        val style = mockk<Style>(relaxUnitFun = true) {
            every { isFullyLoaded } returns true
            every { getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns mockLayer
            every { getImage(ARROW_HEAD_ICON_ADVANCED) } returns mockBitMap
            every { getImage(ARROW_HEAD_ICON_CASING_ADVANCED) } returns mockBitMap
        }
        val firstPoints = listOf(
            Point.fromLngLat(-122.528540, 37.971168),
            Point.fromLngLat(-122.528637, 37.970187),
            Point.fromLngLat(-122.528076, 37.969760)
        )
        val secondPoints = listOf(
            Point.fromLngLat(-122.527418, 37.969325),
            Point.fromLngLat(-122.526409, 37.968767)
        )
        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build()).also {
            it.addArrow(style, firstPoints)
            it.addArrow(style, secondPoints)
        }
        assertEquals(2, arrows.getArrows().size)

        arrows.clearArrows(style)

        assertEquals(0, arrows.getArrows().size)
    }

    @Test
    fun getArrows() {
        val mockBitMap = mockk<Bitmap>()
        val mockSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val mockLayer = mockk<Layer>()
        val style = mockk<Style>(relaxUnitFun = true) {
            every { isFullyLoaded } returns true
            every { getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns mockLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns mockLayer
            every { getImage(ARROW_HEAD_ICON_ADVANCED) } returns mockBitMap
            every { getImage(ARROW_HEAD_ICON_CASING_ADVANCED) } returns mockBitMap
        }

        val points = listOf(
            Point.fromLngLat(-122.528540, 37.971168),
            Point.fromLngLat(-122.528637, 37.970187),
            Point.fromLngLat(-122.528076, 37.969760)
        )

        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())
        arrows.addArrow(style, points)

        assertEquals(1, arrows.getArrows().size)
    }

    @Test
    fun getArrows_includesManeuverArrow() {
        val points = listOf(
            Point.fromLngLat(-122.528540, 37.971168),
            Point.fromLngLat(-122.528637, 37.970187),
            Point.fromLngLat(-122.528076, 37.969760)
        )
        val route = getDirectionsRoute()
        val routeProgress = mockk<RouteProgress> {
            every { currentLegProgress } returns mockk {
                every { legIndex } returns 0
                every { currentStepProgress } returns mockk {
                    every { stepPoints } returns PolylineUtils.decode(
                        route.legs()!![0].steps()!![2].geometry()!!,
                        6
                    )
                    every { distanceTraveled } returns 0f
                    every { step } returns mockk {
                        every { distance() } returns route.legs()!![0].steps()!![2].distance()
                    }
                    every { stepIndex } returns 2
                }
            }
            every { upcomingStepPoints } returns PolylineUtils.decode(
                route.legs()!![0].steps()!![2].geometry()!!,
                6
            )
        }
        val layer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", VISIBLE)
        }
        val mockBitMap = mockk<Bitmap>()
        val mockSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val style = mockk<Style>(relaxUnitFun = true) {
            every { isFullyLoaded } returns true
            every { getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns mockSource
            every { getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns mockSource
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns layer
            every { getImage(ARROW_HEAD_ICON_ADVANCED) } returns mockBitMap
            every { getImage(ARROW_HEAD_ICON_CASING_ADVANCED) } returns mockBitMap
        }

        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build()).also {
            it.addArrow(style, points)
            it.addUpcomingManeuverArrow(style, routeProgress)
        }

        assertEquals(2, arrows.getArrows().size)
    }

    @Test
    fun addUpcomingManeuverArrow() {
        val route = getDirectionsRoute()
        val routeProgress = mockk<RouteProgress> {
            every { currentLegProgress } returns mockk {
                every { legIndex } returns 0
                every { currentStepProgress } returns mockk {
                    every { stepPoints } returns PolylineUtils.decode(
                        route.legs()!![0].steps()!![2].geometry()!!,
                        6
                    )
                    every { distanceTraveled } returns 0f
                    every { step } returns mockk {
                        every { distance() } returns route.legs()!![0].steps()!![2].distance()
                    }
                    every { stepIndex } returns 2
                }
            }
            every { upcomingStepPoints } returns PolylineUtils.decode(
                route.legs()!![0].steps()!![2].geometry()!!,
                6
            )
        }
        val layer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", VISIBLE)
        }
        val mockBitMap = mockk<Bitmap>()
        val shaftSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val headSource = mockk<GeoJsonSource>(relaxUnitFun = true)
        val style = mockk<Style>(relaxUnitFun = true) {
            every { isFullyLoaded } returns true
            every { getSource(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns shaftSource
            every { getSource(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns headSource
            every { getSourceAs<GeoJsonSource>(ARROW_SHAFT_SOURCE_ID_ADVANCED) } returns shaftSource
            every { getSourceAs<GeoJsonSource>(ARROW_HEAD_SOURCE_ID_ADVANCED) } returns headSource
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns layer
            every { getImage(ARROW_HEAD_ICON_ADVANCED) } returns mockBitMap
            every { getImage(ARROW_HEAD_ICON_CASING_ADVANCED) } returns mockBitMap
        }

        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build()).also {
            it.addUpcomingManeuverArrow(style, routeProgress)
        }

        assertEquals(1, arrows.getArrows().size)
        verify(exactly = 2) { shaftSource.setGeoJson(any<FeatureCollection>()) }
        verify(exactly = 2) { headSource.setGeoJson(any<FeatureCollection>()) }
    }

    @Test
    fun arrowsAreVisible_whenLayersAreVisible() {
        val layer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", VISIBLE)
        }
        val style = mockk<Style>(relaxUnitFun = true) {
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns layer
        }
        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())

        val result = arrows.arrowsAreVisible(style)

        assertTrue(result)
    }

    @Test
    fun arrowsAreVisible_whenShaftLayerNotVisible() {
        val layer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", VISIBLE)
        }
        val hiddenLayer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", NONE)
        }
        val style = mockk<Style>(relaxUnitFun = true) {
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns hiddenLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns layer
        }
        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())

        val result = arrows.arrowsAreVisible(style)

        assertFalse(result)
    }

    @Test
    fun arrowsAreVisible_whenShaftCasingLayerNotVisible() {
        val layer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", VISIBLE)
        }
        val hiddenLayer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", NONE)
        }
        val style = mockk<Style>(relaxUnitFun = true) {
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns hiddenLayer
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns layer
        }
        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())

        val result = arrows.arrowsAreVisible(style)

        assertFalse(result)
    }

    @Test
    fun arrowsAreVisible_whenArrowHeadLayerNotVisible() {
        val layer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", VISIBLE)
        }
        val hiddenLayer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", NONE)
        }
        val style = mockk<Style>(relaxUnitFun = true) {
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns hiddenLayer
        }
        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())

        val result = arrows.arrowsAreVisible(style)

        assertFalse(result)
    }

    @Test
    fun arrowsAreVisible_whenArrowHeadCasingLayerNotVisible() {
        val layer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", VISIBLE)
        }
        val hiddenLayer = mockk<Layer> {
            every { visibility } returns PropertyValue("Visibility", NONE)
        }
        val style = mockk<Style>(relaxUnitFun = true) {
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns layer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns hiddenLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns layer
        }
        val arrows = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())

        val result = arrows.arrowsAreVisible(style)

        assertFalse(result)
    }

    @Test
    fun show() {
        val shaftCasingSlot = slot<PropertyValue<String>>()
        val shaftSlot = slot<PropertyValue<String>>()
        val headSlot = slot<PropertyValue<String>>()
        val headCasingSlot = slot<PropertyValue<String>>()

        val shaftCasingLayer = mockk<Layer>(relaxUnitFun = true)
        val shaftLayer = mockk<Layer>(relaxUnitFun = true)
        val headLayer = mockk<Layer>(relaxUnitFun = true)
        val headCasingLayer = mockk<Layer>(relaxUnitFun = true)
        val style = mockk<Style>(relaxUnitFun = true) {
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns shaftCasingLayer
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns shaftLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns headCasingLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns headLayer
        }

        MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build()).show(style)

        verify { shaftCasingLayer.setProperties(capture(shaftCasingSlot)) }
        verify { shaftLayer.setProperties(capture(shaftSlot)) }
        verify { headLayer.setProperties(capture(headSlot)) }
        verify { headCasingLayer.setProperties(capture(headCasingSlot)) }
        assertEquals(VISIBLE, shaftCasingSlot.captured.value)
        assertEquals(VISIBLE, shaftSlot.captured.value)
        assertEquals(VISIBLE, headSlot.captured.value)
        assertEquals(VISIBLE, headCasingSlot.captured.value)
    }

    @Test
    fun hide() {
        val shaftCasingSlot = slot<PropertyValue<String>>()
        val shaftSlot = slot<PropertyValue<String>>()
        val headSlot = slot<PropertyValue<String>>()
        val headCasingSlot = slot<PropertyValue<String>>()

        val shaftCasingLayer = mockk<Layer>(relaxUnitFun = true)
        val shaftLayer = mockk<Layer>(relaxUnitFun = true)
        val headLayer = mockk<Layer>(relaxUnitFun = true)
        val headCasingLayer = mockk<Layer>(relaxUnitFun = true)
        val style = mockk<Style>(relaxUnitFun = true) {
            every { getLayer(ARROW_SHAFT_CASING_LINE_LAYER_ID_ADVANCED) } returns shaftCasingLayer
            every { getLayer(ARROW_SHAFT_LINE_LAYER_ID_ADVANCED) } returns shaftLayer
            every { getLayer(ARROW_HEAD_CASING_LAYER_ID_ADVANCED) } returns headCasingLayer
            every { getLayer(ARROW_HEAD_LAYER_ID_ADVANCED) } returns headLayer
        }

        MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build()).hide(style)

        verify { shaftCasingLayer.setProperties(capture(shaftCasingSlot)) }
        verify { shaftLayer.setProperties(capture(shaftSlot)) }
        verify { headLayer.setProperties(capture(headSlot)) }
        verify { headCasingLayer.setProperties(capture(headCasingSlot)) }
        assertEquals(NONE, shaftCasingSlot.captured.value)
        assertEquals(NONE, shaftSlot.captured.value)
        assertEquals(NONE, headSlot.captured.value)
        assertEquals(NONE, headCasingSlot.captured.value)
    }

    @Test
    fun obtainArrowPointsFrom() {
        val route = getDirectionsRoute()
        val routeProgress = mockk<RouteProgress> {
            every { currentLegProgress } returns mockk {
                every { legIndex } returns 0
                every { currentStepProgress } returns mockk {
                    every { stepPoints } returns PolylineUtils.decode(
                        route.legs()!![0].steps()!![2].geometry()!!,
                        6
                    )
                    every { distanceTraveled } returns 0f
                    every { step } returns mockk {
                        every { distance() } returns route.legs()!![0].steps()!![2].distance()
                    }
                    every { stepIndex } returns 2
                }
            }
            every { upcomingStepPoints } returns PolylineUtils.decode(
                route.legs()!![0].steps()!![2].geometry()!!,
                6
            )
        }

        val result = MapboxRouteArrows(RouteArrowOptions.Builder(ctx, styleRes).build())
            .obtainArrowPointsFrom(routeProgress)

        assertEquals(5, result.size)
        assertEquals(
            Point.fromLngLat(-122.52565322662127, 37.97369128820272), result.first()
        )
        assertEquals(
            Point.fromLngLat(-122.52566578202115, 37.973692460043715), result.last()
        )
    }

    private fun getDirectionsRoute(): DirectionsRoute {
        val tokenHere = "someToken"
        val directionsRouteAsJson = loadJsonFixture("vanish_point_test.txt")
            .replace("tokenHere", tokenHere)

        return DirectionsRoute.fromJson(directionsRouteAsJson)
    }
}
