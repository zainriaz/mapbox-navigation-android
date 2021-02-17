package com.mapbox.navigation.core.accounts

internal interface NavigationTokenGenerator {
    fun getSKUToken(): String
}
