package com.mapbox.navigation.examples.ui.exp

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.navigation.examples.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UICore private constructor(lifecycle: Lifecycle, private val context: Context): LifecycleObserver {

    constructor(frag: Fragment): this(frag.lifecycle, frag.context!!)
    constructor(act: FragmentActivity): this(act.lifecycle, act)

    private val job = SupervisorJob()
    internal val uiScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)

    init {
        lifecycle.addObserver(this)
        observeViewUpdates()
    }

    private val viewModel: UICoreViewModel by lazy {
        ViewModelProvider(context as ViewModelStoreOwner, UICoreViewModelFactory()).get(UICoreViewModel::class.java)
    }

    private val parentActivity: Activity by lazy {
        when (context) {
            is Fragment -> context.activity as Activity
            else -> context as Activity
        }
    }

    private val mapView: MapView by lazy {
        parentActivity.window.findViewById(R.id.mapView)
    }

    fun observeViewUpdates() {
        uiScope.launch {
            for(cmd in viewModel.getViewCommandUpdates()) {
                cmd.invoke(parentActivity)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun handleOnCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun handleOnResume() {
        viewModel.resumeExample("i'm resuming.")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun handleOnStop() {
        job.cancel()
    }
}