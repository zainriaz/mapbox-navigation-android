package com.mapbox.navigation.ui.base.api.soundbutton

import androidx.annotation.UiThread
import com.mapbox.navigation.ui.base.api.Api
import com.mapbox.navigation.ui.base.model.soundbutton.SoundButtonState

@UiThread
interface SoundButtonApi: Api<SoundButtonState, SoundButtonStateObserver> {

    fun onSoundButtonClicked()
}
