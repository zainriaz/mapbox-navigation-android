package com.mapbox.navigation.ui.base.model.soundbutton

import com.mapbox.navigation.ui.base.MapboxState

sealed class SoundButtonState : MapboxState {
    data class SoundEnabled(val enable: Boolean) : SoundButtonState()
}
