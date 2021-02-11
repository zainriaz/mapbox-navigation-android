package com.mapbox.navigation.examples.core.dropin.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mapbox.navigation.examples.core.dropin.UIDropInViewModel

class UIDropInViewModelFactory(private val dependencyFactory: UIDropInDependencyFactory) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UIDropInViewModel::class.java)) {
            return UIDropInViewModel(
                dependencyFactory
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
