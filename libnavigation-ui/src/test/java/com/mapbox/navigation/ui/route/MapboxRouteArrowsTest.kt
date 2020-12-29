package com.mapbox.navigation.ui.route

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.navigation.ui.R
import com.mapbox.navigation.ui.internal.ThemeSwitcher
import io.mockk.mockk
import org.junit.Assert.assertEquals
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

    //{"type":"LineString","coordinates":[[-122.523669,37.975386],[-122.523729,37.975194],[-122.523729,37.975194],[-122.523579,37.975173],[-122.5233922,37.9751463]]}
    //{"type":"Feature","geometry":{"type":"LineString","coordinates":[[-122.523669,37.975386],[-122.523729,37.975194],[-122.523729,37.975194],[-122.523579,37.975173],[-122.5233922,37.9751463]]}}
    //{"type":"Feature","geometry":{"type":"LineString","coordinates":[[-122.523669,37.975386],[-122.523729,37.975194],[-122.523729,37.975194],[-122.523579,37.975173],[-122.5233922,37.9751463]]}}

    //{"type":"Feature","geometry":{"type":"Point","coordinates":[-122.5233922,37.9751463]},"properties":{"mapbox-navigation-arrow-bearing":100.271904}}
    //{"type":"Feature","geometry":{"type":"Point","coordinates":[-122.5233922,37.9751463]},"properties":{"mapbox-navigation-arrow-bearing":100.27190701832444}}
    @Test
    fun addArrow_doesNotAddDuplicates() {
        val tmpList = listOf(
            Point.fromLngLat(-122.523669, 37.975386),
            Point.fromLngLat(-122.523729, 37.975194),
            Point.fromLngLat(-122.523729, 37.975194),
            Point.fromLngLat(-122.523579, 37.975173),
            Point.fromLngLat(-122.52339223607466, 37.97514631967111)
            //Point.fromLngLat(),
            //Point.fromLngLat(-122.522308, 37.974121)
        )
        /*
        Point{type=Point, bbox=null, coordinates=[-122.523669, 37.975386]}
        Point{type=Point, bbox=null, coordinates=[-122.523729, 37.975194]}
        Point{type=Point, bbox=null, coordinates=[-122.523729, 37.975194]}
        Point{type=Point, bbox=null, coordinates=[-122.523579, 37.975173]}
        Point{type=Point, bbox=null, coordinates=[-122.52339223607466, 37.97514631967111]}
         */

        val style = mockk<Style>()
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
        //arrows.addArrow(style, firstPoints)
        //arrows.addArrow(style, secondPoints)

        arrows.addArrow(style, tmpList)

        assertEquals(1, arrows.getArrows().size)
    }

    @Test
    fun removeArrow() {
        val style = mockk<Style>()
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
        val style = mockk<Style>()
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
}