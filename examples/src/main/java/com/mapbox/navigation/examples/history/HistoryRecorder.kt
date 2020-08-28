package com.mapbox.navigation.examples.history

import com.mapbox.navigation.core.MapboxNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.zip.GZIPOutputStream

/**
 * Example showing how to record navigation history and safe it to a file.
 *
 * To access the file in Android Studio
 *  Go to View > Tools Windows > Device File Explorer > data > com.mapbox.navigation.examples > cache > history-cache
 */
class HistoryRecorder(
    private val navigation: MapboxNavigation
) {
    private var startedAt: Date? = null

    fun startRecording() {
        startedAt = Date()
        navigation.toggleHistory(true)
    }

    suspend fun stopRecording(): File = withContext(Dispatchers.IO) {
        val history = navigation.retrieveHistory()
        navigation.toggleHistory(false)
        writeHistoryToFile(history)
    }

    private fun writeHistoryToFile(history: String): File {
        val cacheDirectory = navigation.navigationOptions.applicationContext.cacheDir
        val historyDirectory = File(cacheDirectory, "history-cache")
            .also { it.mkdirs() }

        val file = createTempFile(createFilename(), ".json.gz", historyDirectory)
        file.outputStream().use { fos ->
            GZIPOutputStream(fos).use { gzip ->
                gzip.write(history.toByteArray())
            }
        }
        Timber.i("History file saved to ${file.absolutePath}")
        return file
    }

    private fun createFilename(): String =
        "${utcFormatter.format(startedAt!!)}_${utcFormatter.format(Date())}_"

    private val utcFormatter = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.US)
        .also { it.timeZone = TimeZone.getTimeZone("UTC") }
}
