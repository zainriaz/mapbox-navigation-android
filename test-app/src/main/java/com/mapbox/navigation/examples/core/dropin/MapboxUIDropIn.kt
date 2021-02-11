package com.mapbox.navigation.examples.core.dropin



import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapLoadError
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.delegates.listeners.OnMapLoadErrorListener
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.getGesturesPlugin
import com.mapbox.maps.plugin.location.LocationComponentActivationOptions
import com.mapbox.maps.plugin.location.LocationPluginImpl
import com.mapbox.maps.plugin.location.LocationUpdate
import com.mapbox.maps.plugin.location.getLocationPlugin
import com.mapbox.maps.plugin.location.modes.RenderMode
import com.mapbox.navigation.base.internal.route.RouteUrl
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.history.ReplayEventBase
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.replay.route.ReplayRouteMapper
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.examples.core.TripProgressActivity
import com.mapbox.navigation.examples.core.dropin.factory.UIDropInDependencyFactory
import com.mapbox.navigation.examples.core.dropin.factory.UIDropInViewModelFactory
import java.lang.ref.WeakReference

internal class MapboxUIDropIn(private val uiDropInDependencyFactory: UIDropInDependencyFactory) : LifecycleObserver, UIDropIn {

    private val mapboxReplayer = MapboxReplayer()
    private val replayRouteMapper = ReplayRouteMapper()
    private lateinit var locationComponent: LocationPluginImpl
    private val replayProgressObserver = ReplayProgressObserver(mapboxReplayer)

    init {
        uiDropInDependencyFactory.lifecycle.addObserver(this)
    }

    private val navigationOptions by lazy {
        NavigationOptions.Builder(uiDropInDependencyFactory.getApplicationContext())
            .accessToken(getMapboxAccessTokenFromResources())
            .locationEngine(ReplayLocationEngine(mapboxReplayer))
            .build()
    }

    private val mapboxNavigation: MapboxNavigation by lazy {
        MapboxNavigation(navigationOptions)
    }

    private val viewModel: UIDropInViewModel by lazy {
        ViewModelProvider(
            uiDropInDependencyFactory.viewModelStoreOwner,
            UIDropInViewModelFactory(
                uiDropInDependencyFactory
            )
        ).get(UIDropInViewModel::class.java).also {
            it.initialize()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private fun handleOnCreate() {
        locationComponent = getLocationComponent()
        initStyle()

        mapboxNavigation.registerLocationObserver(locationObserver)
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.registerRouteProgressObserver(replayProgressObserver)
        mapboxNavigation.registerRoutesObserver(routesObserver)
        mapboxReplayer.pushRealLocation(uiDropInDependencyFactory.getApplicationContext(), 0.0)
        mapboxReplayer.playbackSpeed(1.5)
        mapboxReplayer.play()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun handleOnStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun handleOnResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun handleOnStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun handleOnDestroy() {
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.unregisterRouteProgressObserver(replayProgressObserver)
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.onDestroy()
    }

    private fun startSimulation(route: DirectionsRoute) {
        mapboxReplayer.stop()
        mapboxReplayer.clearEvents()
        val replayData: List<ReplayEventBase> = replayRouteMapper.mapDirectionsRouteGeometry(route)
        mapboxReplayer.pushEvents(replayData)
        mapboxReplayer.seekTo(replayData[0])
        mapboxReplayer.play()
    }

    override fun saveStateWith(key: String, outState: Bundle) {
        viewModel.saveStateWith(key, outState)
    }

    override fun drawRoute(route: DirectionsRoute) {
        viewModel.drawRoutes(listOf(route))
    }

    @SuppressLint("MissingPermission")
    override fun startNavigation() {
        locationComponent.renderMode  = RenderMode.GPS
        mapboxNavigation.startTripSession()
        startSimulation(mapboxNavigation.getRoutes()[0])
    }

    @SuppressLint("MissingPermission")
    private fun initStyle() {
        uiDropInDependencyFactory.getMapView().getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS,
            Style.OnStyleLoaded { style: Style ->
                initializeLocationComponent(style)
                mapboxNavigation.navigationOptions.locationEngine.getLastLocation(
                    locationEngineCallback
                )
                uiDropInDependencyFactory.getMapView().getGesturesPlugin()
                    .addOnMapLongClickListener(
                        mapLongClickListener
                    )
            },
            object : OnMapLoadErrorListener {
                @SuppressLint("LogNotTimber")
                override fun onMapLoadError(mapLoadError: MapLoadError, msg: String) {
                    Log.e(
                        TripProgressActivity::class.java.simpleName,
                        "Error loading map: " + mapLoadError.name
                    )
                }
            }
        )
    }

    private fun initializeLocationComponent(style: Style) {
        val activationOptions = LocationComponentActivationOptions.builder(
            uiDropInDependencyFactory.getApplicationContext(),
            style
        )
            .useDefaultLocationEngine(false)
            .build()
        locationComponent.activateLocationComponent(activationOptions)
        locationComponent.enabled = true
        locationComponent.renderMode = RenderMode.COMPASS
    }

    private fun getMapboxAccessTokenFromResources(): String {
        return uiDropInDependencyFactory.getApplicationContext().getString(
            uiDropInDependencyFactory.getApplicationContext().resources.getIdentifier(
                "mapbox_access_token",
                "string",
                uiDropInDependencyFactory.getApplicationContext().packageName
            )
        )
    }

    private val mapLongClickListener = OnMapLongClickListener { point ->
        val currentLocation = uiDropInDependencyFactory.getMapView().getLocationPlugin().lastKnownLocation
        if (currentLocation != null) {
            val originPoint = Point.fromLngLat(
                currentLocation.longitude,
                currentLocation.latitude
            )
            findRoute(originPoint, point)
        }

        false
    }

    private fun findRoute(origin: Point?, destination: Point?) {
        val routeOptions = RouteOptions.builder()
            .baseUrl(RouteUrl.BASE_URL)
            .user(RouteUrl.PROFILE_DEFAULT_USER)
            .profile(RouteUrl.PROFILE_DRIVING_TRAFFIC)
            .geometries(RouteUrl.GEOMETRY_POLYLINE6)
            .requestUuid("")
            .accessToken(getMapboxAccessTokenFromResources())
            .coordinates(listOf(origin, destination))
            .alternatives(true)
            .build()
        mapboxNavigation.requestRoutes(
            routeOptions,
            routesReqCallback
        )
    }

    private val routesReqCallback: RoutesRequestCallback = object : RoutesRequestCallback {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            //
        }

        @SuppressLint("LogNotTimber")
        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            Log.e(
                TripProgressActivity::class.java.simpleName,
                "route request failure $throwable"
            )
        }

        @SuppressLint("LogNotTimber")
        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            Log.d(MapboxUIDropIn::class.java.simpleName, "route request canceled")
        }
    }

    private val routeProgressObserver = object : RouteProgressObserver {
        override fun onRouteProgressChanged(routeProgress: RouteProgress) {
            viewModel.processRouteProgressUpdate(routeProgress)
        }
    }

    private val routesObserver = object: RoutesObserver {
        override fun onRoutesChanged(routes: List<DirectionsRoute>) {
            viewModel.drawRoutes(routes)
        }
    }

    private fun getLocationComponent(): LocationPluginImpl {
        return uiDropInDependencyFactory.getMapView().getLocationPlugin()
    }

    private val locationEngineCallback = MyLocationEngineCallback(WeakReference(this))

    private class MyLocationEngineCallback(
        private val dropInRef: WeakReference<MapboxUIDropIn>
    ) : LocationEngineCallback<LocationEngineResult> {

        override fun onSuccess(result: LocationEngineResult?) {
            val location = result!!.lastLocation
            val dropIn = dropInRef.get()
            if (location != null && dropIn != null) {
                val point = Point.fromLngLat(location.longitude, location.latitude)
                val cameraOptions = CameraOptions.Builder().center(point).zoom(13.0).build()
                dropIn.uiDropInDependencyFactory.getMapView().getMapboxMap().jumpTo(cameraOptions)
                dropIn.locationComponent.forceLocationUpdate(location)
            }
        }

        @SuppressLint("LogNotTimber")
        override fun onFailure(exception: Exception) {
            Log.i(MapboxUIDropIn::class.java.simpleName, exception.toString())
        }
    }

    private val locationObserver: LocationObserver = object : LocationObserver {
        @SuppressLint("LogNotTimber")
        override fun onRawLocationChanged(rawLocation: Location) {
            Log.d(
                TripProgressActivity::class.java.simpleName,
                "raw location $rawLocation"
            )
        }

        override fun onEnhancedLocationChanged(
            enhancedLocation: Location,
            keyPoints: List<Location>
        ) {
            if (keyPoints.isEmpty()) {
                updateLocation(listOf(enhancedLocation))
            } else {
                updateLocation(keyPoints)
            }
        }
    }

    private fun updateLocation(locations: List<Location>) {
        val location = locations[0]
        val locationUpdate = LocationUpdate(location, null, null)
        locationComponent.forceLocationUpdate(locationUpdate)

        val mapAnimationOptionsBuilder = MapAnimationOptions.Builder()
        mapAnimationOptionsBuilder.duration = 1500L
        uiDropInDependencyFactory.getCamera().easeTo(
            CameraOptions.Builder()
                .center(Point.fromLngLat(location.longitude, location.latitude))
                .bearing(location.bearing.toDouble())
                .pitch(45.0)
                .zoom(17.0)
                .padding(EdgeInsets(1000.0, 0.0, 0.0, 0.0))
                .build(),
            mapAnimationOptionsBuilder.build()
        )
    }
}

