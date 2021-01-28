package com.mapbox.navigation.ui.voice.soundbutton.api

import com.mapbox.navigation.ui.base.api.MapboxApi
import com.mapbox.navigation.ui.base.api.MapboxStateObserver
import com.mapbox.navigation.ui.base.api.soundbutton.SoundButtonApi
import com.mapbox.navigation.ui.base.api.soundbutton.SoundButtonStateObserver
import com.mapbox.navigation.ui.base.model.soundbutton.SoundButtonState
import com.mapbox.navigation.ui.voice.soundbutton.SoundButtonAction
import com.mapbox.navigation.ui.voice.soundbutton.SoundButtonProcessor
import com.mapbox.navigation.ui.voice.soundbutton.SoundButtonResult

class MapboxSoundButtonApi(
    initialState: SoundButtonState
) : MapboxApi<SoundButtonState, SoundButtonStateObserver>(initialState), SoundButtonApi {

    override fun onSoundButtonClicked() {
        val action = when (val currentStateLocal = state) {
            is SoundButtonState.SoundEnabled -> {
                if (currentStateLocal.enable) {
                    SoundButtonAction.MuteSound(true)
                } else {
                    SoundButtonAction.MuteSound(false)
                }
            }
        }
        processResult(SoundButtonProcessor.process(action))
    }

    private fun processResult(result: SoundButtonResult) {
        val newState = when (result) {
            SoundButtonResult.MuteSound ->
                SoundButtonState.SoundEnabled(false)
            SoundButtonResult.UnMuteSound ->
                SoundButtonState.SoundEnabled(true)
        }
        state = newState
    }
}
