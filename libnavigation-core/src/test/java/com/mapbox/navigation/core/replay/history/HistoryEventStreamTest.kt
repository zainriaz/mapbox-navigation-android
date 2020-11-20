package com.mapbox.navigation.core.replay.history

import com.google.gson.stream.JsonReader
import org.apache.commons.io.IOUtils
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.InputStreamReader

@RunWith(RobolectricTestRunner::class)
class HistoryEventStreamTest {

    @get:Rule
    val memoryTestRule = MemoryTestRule()

    private val historyFile = "history-events-file.json"

    @Test
    fun `read file as a stream`() {
        val historyEventStream = resourceAsHistoryEventStream(historyFile)

        val replayEvents = historyEventStream.read(10)

        println("memoryUsed: ${memoryTestRule.memoryUsedMB}")
        assertEquals(10, replayEvents.size)
    }

    @Test
    fun `read entire file as a stream`() {
        val historyEventStream = resourceAsHistoryEventStream(historyFile)

        val replayEvents = historyEventStream.read(580)
            .filterIsInstance<ReplayEventUpdateLocation>()

        println("memoryUsed: ${memoryTestRule.memoryUsedMB}")
        assertEquals(180, replayEvents.size)
    }

    @Test
    fun `read file as a string`() {
        val historyString = resourceAsString(historyFile)
        val replayHistoryMapper = ReplayHistoryMapper()
        val replayEvents = replayHistoryMapper.mapToReplayEvents(historyString)

        println("memoryUsed: ${memoryTestRule.memoryUsedMB}")
        assertEquals(363, replayEvents.size)
    }

    private fun resourceAsHistoryEventStream(
        name: String,
        packageName: String = "com.mapbox.navigation.core.replay.history"
    ): HistoryEventStream {
        val inputStream = javaClass.classLoader?.getResourceAsStream("$packageName/$name")
        val jsonReader = JsonReader(InputStreamReader(inputStream!!))
        return HistoryEventStream(jsonReader)
    }

    private fun resourceAsString(
        name: String,
        packageName: String = "com.mapbox.navigation.core.replay.history"
    ): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream("$packageName/$name")
        return IOUtils.toString(inputStream, "UTF-8")
    }
}
