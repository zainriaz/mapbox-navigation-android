package com.mapbox.navigation.ui.voice.soundbutton

internal object SoundButtonProcessor {

    fun process(action: SoundButtonAction): SoundButtonResult {
        when (action) {
            is SoundButtonAction.MuteSound -> {
                return if (action.mute) {
                    SoundButtonResult.MuteSound
                } else {
                    SoundButtonResult.UnMuteSound
                }
            }
        }
    }
}
