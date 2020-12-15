package com.mapbox.navigation.core.replay.history

import com.mapbox.navigation.core.replay.MapboxReplayer

/**
 * This class is responsible for queuing events into the MapboxReplayer. It will take events
 * out of a file and push them into replay. This creates a lower memory implementation
 * for replaying history files.
 */
class ReplayEventBuffer(
    private val mapboxReplayer: MapboxReplayer,
    private val replayEventStream: ReplayEventStream
) : ReplayEventsObserver {

    private var queuedEvents = 0

    override fun replayEvents(events: List<ReplayEventBase>) {
        queuedEvents -= events.size
        if (queuedEvents < MIN_QUEUE_SIZE && replayEventStream.hasNext()) {
            pushEvents()
        }
    }

    fun pushEvents() {
        val replayEvents = replayEventStream.asSequence()
            .take(PUSH_EVENTS_COUNT).toList()
        queuedEvents += replayEvents.size
        mapboxReplayer.pushEvents(replayEvents)
    }

    companion object {
        private const val MIN_QUEUE_SIZE = 50
        private const val PUSH_EVENTS_COUNT = 100
    }
}
