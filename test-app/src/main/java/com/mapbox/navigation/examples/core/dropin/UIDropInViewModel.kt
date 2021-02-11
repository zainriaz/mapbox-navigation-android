package com.mapbox.navigation.examples.core.dropin

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.examples.core.dropin.factory.UINavigationViewModelDependencies
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.navigation.ui.utils.internal.ifNonNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UIDropInViewModel(private val dependencyFactory: UINavigationViewModelDependencies) : ViewModel() {

    fun initialize() {

    }

    override fun onCleared() {
        super.onCleared()
    }

    fun saveStateWith(key: String, outState: Bundle) {
        // todo
    }

    fun drawRoutes(routes: List<DirectionsRoute>) = viewModelScope.launch {
        ifNonNull(dependencyFactory.getMapView().getMapboxMap().getStyle()) { style ->
            val newRoutes = routes.map { RouteLine(it, null) }
            val deferredState = async(Dispatchers.Default) {
                dependencyFactory.getRouteLineApi().setRoutes(newRoutes)
            }
            dependencyFactory.getRouteLineView().render(style, deferredState.await())
        }
    }

    fun processRouteProgressUpdate(routeProgress: RouteProgress) = viewModelScope.launch {
        dependencyFactory.getRouteLineApi().updateWithRouteProgress(routeProgress)

        val currentRoute = routeProgress.route
        val hasGeometry = (currentRoute.geometry() != null && currentRoute.geometry()!!.isNotEmpty())
        val isNewRoute = (hasGeometry && currentRoute != dependencyFactory.getRouteLineApi().getPrimaryRoute())

        ifNonNull(dependencyFactory.getMapView().getMapboxMap().getStyle()) { style ->
            val deferredRouteLineState = if (isNewRoute) {
                async(Dispatchers.Default) {
                    dependencyFactory.getRouteLineApi().setRoutes(listOf(RouteLine(routeProgress.route, null)))
                }
            } else {
                null
            }

            val deferredArrowState = async(Dispatchers.Default) {
                dependencyFactory.getRouteArrowApi().updateUpcomingManeuverArrow(routeProgress)
            }

            val tripProgressDeferredState = async(Dispatchers.Default) {
                dependencyFactory.getApiProvider().getTripProgressApi()?.first?.getTripProgress(routeProgress)
            }


            deferredRouteLineState?.let {
                dependencyFactory.getRouteLineView().render(style, it.await())
            }
            dependencyFactory.getRouteArrowView().render(style, deferredArrowState.await())
            tripProgressDeferredState.await()?.let {
                dependencyFactory.getApiProvider().getTripProgressApi()?.second?.render(it)
            }
        }
    }
}
