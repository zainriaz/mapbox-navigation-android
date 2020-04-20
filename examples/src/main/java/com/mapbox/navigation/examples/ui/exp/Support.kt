package com.mapbox.navigation.examples.ui.exp

import android.app.Activity

typealias ActivityStateCmd<T> = (activity: T) -> Unit
typealias ViewEffect<T1,T2> = (input: T1) -> ActivityStateCmd<T2>

