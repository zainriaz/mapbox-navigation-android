package com.mapbox.navigation.examples.core

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.MapboxMapOptions
import com.mapbox.maps.ResourceOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.OnMapLongClickListener
import com.mapbox.maps.plugin.gestures.getGesturesPlugin
import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions
import com.mapbox.navigation.examples.core.dropin.ApiProvider
import com.mapbox.navigation.examples.core.dropin.MapboxUIDropIn
import com.mapbox.navigation.examples.core.dropin.factory.UIDropInDependencyFactory
import com.mapbox.navigation.ui.base.model.tripprogress.DistanceRemainingFormatter
import com.mapbox.navigation.ui.base.model.tripprogress.EstimatedTimeToArrivalFormatter
import com.mapbox.navigation.ui.base.model.tripprogress.PercentDistanceTraveledFormatter
import com.mapbox.navigation.ui.base.model.tripprogress.TimeRemainingFormatter
import com.mapbox.navigation.ui.base.model.tripprogress.TripProgressUpdateFormatter
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import kotlinx.android.synthetic.main.trip_progress_activity_layout.*

class DropInActivity : AppCompatActivity(), OnMapLongClickListener {

    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var dropIn: MapboxUIDropIn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip_progress_activity_layout)
        addMap()
        mapboxMap = mapView.getMapboxMap()
        init()
    }

    private fun init() {
        val apiProvider = ApiProvider.Builder().withTripProgress(
            MapboxTripProgressApi(getTripProgressFormatter()),
            tripProgressView
        ).build()

        val dropInDependencyFactory = UIDropInDependencyFactory(
            this,
            mapView,
            apiProvider
        )
        dropIn = MapboxUIDropIn(dropInDependencyFactory)

        initStyle()
        initListeners()
    }

    private fun initStyle() {
        mapboxMap.getStyle(Style.OnStyleLoaded { style ->
            mapView.getGesturesPlugin().addOnMapLongClickListener(this)
        })
    }

    @SuppressLint("MissingPermission")
    private fun initListeners() {
        startNavigation.setOnClickListener {
            dropIn.startNavigation()
            startNavigation.visibility = View.GONE
            tripProgressView.visibility = View.VISIBLE // fixme
        }
    }

    private fun getTripProgressFormatter(): TripProgressUpdateFormatter {
        val distanceRemainingFormatter =
            DistanceRemainingFormatter(DistanceFormatterOptions.Builder(this).build())

        return TripProgressUpdateFormatter.Builder(this)
            .distanceRemainingFormatter(distanceRemainingFormatter)
            .timeRemainingFormatter(TimeRemainingFormatter(this))
            .percentRouteTraveledFormatter(PercentDistanceTraveledFormatter())
            .estimatedTimeToArrivalFormatter(
                EstimatedTimeToArrivalFormatter(
                    this,
                    TimeFormat.NONE_SPECIFIED
                )
            ).build()
    }

    private fun addMap() {
        val mapboxMapOptions = MapboxMapOptions(this, resources.displayMetrics.density, null)
        val resourceOptions = ResourceOptions.Builder()
            .accessToken(getMapboxAccessTokenFromResources())
            .assetPath(filesDir.absolutePath)
            .cachePath(filesDir.absolutePath + "/mbx.db")
            .cacheSize(100000000L) // 100 MB
            .tileStorePath(filesDir.absolutePath + "/maps_tile_store/")
            .build()
        mapboxMapOptions.resourceOptions = resourceOptions
        mapView = MapView(this, mapboxMapOptions)
        mapView_container.addView(mapView)
    }

    private fun getMapboxAccessTokenFromResources(): String {
        return getString(this.resources.getIdentifier("mapbox_access_token", "string", packageName))
    }

    override fun onMapLongClick(point: Point): Boolean {
        vibrate()
        startNavigation.visibility = View.VISIBLE // cheating a little here
        return false
    }

    @SuppressLint("MissingPermission")
    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(100L)
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
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
