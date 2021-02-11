package com.mapbox.navigation.examples.core.dropin.factory

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin
import com.mapbox.maps.plugin.animation.getCameraAnimationsPlugin
import com.mapbox.navigation.examples.core.dropin.ApiProvider
import com.mapbox.navigation.ui.base.MapboxView
import com.mapbox.navigation.ui.base.api.tripprogress.TripProgressApi
import com.mapbox.navigation.ui.base.internal.route.RouteConstants.PRIMARY_ROUTE_TRAFFIC_LAYER_ID
import com.mapbox.navigation.ui.base.model.tripprogress.TripProgressState
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources

class UIDropInDependencyFactory private constructor(
    val lifecycle: Lifecycle,
    val viewModelStoreOwner: ViewModelStoreOwner,
    private val mapView: MapView,
    private val apiProvider: ApiProvider
) : UINavigationViewModelDependencies {

    constructor(fragment: Fragment, mapView: MapView, apiProvider: ApiProvider) : this(fragment.lifecycle, fragment, mapView, apiProvider)
    constructor(activity: FragmentActivity, mapView: MapView, apiProvider: ApiProvider) : this(activity.lifecycle, activity, mapView, apiProvider)

    private val routeLineResources by lazy {
        RouteLineResources.Builder().build()
    }

    private val mapboxRouteLineOptions by lazy {
        MapboxRouteLineOptions.Builder(getApplicationContext())
            .withRouteLineResources(routeLineResources)
            .build()
    }

    private val routelineApi by lazy {
        MapboxRouteLineApi(mapboxRouteLineOptions)
    }

    private val mapboxRouteLineView by lazy {
        MapboxRouteLineView(mapboxRouteLineOptions)
    }

    override fun getRouteLineView() = mapboxRouteLineView

    private val routeArrowOptions by lazy {
        RouteArrowOptions.Builder(getApplicationContext())
            .withAboveLayerId(PRIMARY_ROUTE_TRAFFIC_LAYER_ID)
            .build()
    }

    private val mapboxRouteArrowApi = MapboxRouteArrowApi()

    private val mapboxRouteArrowView by lazy {
        MapboxRouteArrowView(routeArrowOptions)
    }

    private val mapboxCamera by lazy {
        mapView.getCameraAnimationsPlugin()
    }

    override fun getRouteArrowApi() = mapboxRouteArrowApi

    override fun getRouteArrowView() = mapboxRouteArrowView

    override fun getRouteLineApi(): MapboxRouteLineApi = routelineApi

    override fun getApplicationContext(): Context = mapView.context.applicationContext

    override fun getMapView(): MapView = mapView

    override fun getApiProvider(): ApiProvider = apiProvider

    override fun getCamera(): CameraAnimationsPlugin = mapboxCamera
}

interface UINavigationViewModelDependencies {
    fun getApplicationContext(): Context
    fun getRouteLineApi(): MapboxRouteLineApi
    fun getRouteLineView(): MapboxRouteLineView
    fun getRouteArrowApi(): MapboxRouteArrowApi
    fun getRouteArrowView(): MapboxRouteArrowView
    fun getMapView(): MapView
    fun getApiProvider(): ApiProvider
    fun getCamera(): CameraAnimationsPlugin
}
