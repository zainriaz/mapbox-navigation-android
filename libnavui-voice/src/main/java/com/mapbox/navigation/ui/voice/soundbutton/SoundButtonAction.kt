package com.mapbox.navigation.ui.voice.soundbutton

internal sealed class SoundButtonAction {

    data class MuteSound(val mute: Boolean) : SoundButtonAction()
}
