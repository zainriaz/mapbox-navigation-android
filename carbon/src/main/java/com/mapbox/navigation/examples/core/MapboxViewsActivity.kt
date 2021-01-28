package com.mapbox.navigation.examples.core

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.examples.util.Utils
import com.mapbox.navigation.ui.base.api.soundbutton.SoundButtonApi
import com.mapbox.navigation.ui.base.api.soundbutton.SoundButtonStateObserver
import com.mapbox.navigation.ui.base.model.soundbutton.SoundButtonState
import com.mapbox.navigation.ui.voice.soundbutton.api.MapboxSoundButtonApi
import kotlinx.android.synthetic.main.layout_activity_views.*

class MapboxViewsActivity : AppCompatActivity() {

    private val mapboxMap: MapboxMap by lazy { mapView.getMapboxMap() }
    private val soundButtonApi: SoundButtonApi =
        MapboxSoundButtonApi(SoundButtonState.SoundEnabled(true))

    private val mapboxNavigation by lazy {
        MapboxNavigation(
            MapboxNavigation.defaultNavigationOptionsBuilder(
                this, Utils.getMapboxAccessToken(this)
            )
                .build()
        )
    }

    private val locationEngineCallback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation?.let { lastLocation ->
                val point = Point.fromLngLat(lastLocation.longitude, lastLocation.latitude)
                val cameraOptions = CameraOptions.Builder().center(point).zoom(13.0).build()
                mapboxMap.jumpTo(cameraOptions)
            }
        }

        override fun onFailure(exception: Exception) = Unit
    }

    private val soundButtonClickListener = View.OnClickListener {
        soundButtonApi.onSoundButtonClicked()
    }

    private val soundButtonStateObserver = object : SoundButtonStateObserver {

        override fun onStateChanged(newState: SoundButtonState) {
            soundButton.render(newState)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_views)
        initSoundButton()
        initStyle()
    }

    private fun initSoundButton() {
        soundButton.addOnClickListener(soundButtonClickListener)
        soundButtonApi.observeState(soundButtonStateObserver)
    }

    @SuppressLint("MissingPermission")
    private fun initStyle() {
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
            mapboxNavigation.navigationOptions.locationEngine.getLastLocation(locationEngineCallback)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundButton.removeOnClickListener(soundButtonClickListener)
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
