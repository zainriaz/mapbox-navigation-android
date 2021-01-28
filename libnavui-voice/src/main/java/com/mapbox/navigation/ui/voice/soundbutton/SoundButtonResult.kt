package com.mapbox.navigation.ui.voice.soundbutton

internal sealed class SoundButtonResult {

    object MuteSound : SoundButtonResult()

    object UnMuteSound : SoundButtonResult()
}
