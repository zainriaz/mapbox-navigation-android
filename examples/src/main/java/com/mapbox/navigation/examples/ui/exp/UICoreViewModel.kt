package com.mapbox.navigation.examples.ui.exp

import android.app.Activity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

class UICoreViewModel: ViewModel() {

    private val viewCommands = Channel<ActivityStateCmd<Activity>>(Channel.UNLIMITED)
    fun getViewCommandUpdates(): ReceiveChannel<ActivityStateCmd<Activity>> = viewCommands

    fun resumeExample(input: String) {
        val result = someExampleInteractor(input)
        val cmd = resumeExampleViewUpdateCmd(result)
        listOf(cmd).emit()
    }

    private fun List<ActivityStateCmd<Activity>>.emit() {
        this.forEach { cmd ->
            if(!viewCommands.isClosedForSend) {
                viewCommands.offer(cmd)
            }
        }
    }
}