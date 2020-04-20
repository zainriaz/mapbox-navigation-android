package com.mapbox.navigation.examples.ui.exp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UICoreViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
       if (modelClass.isAssignableFrom(UICoreViewModel::class.java)) {
           return UICoreViewModel() as T
       }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}