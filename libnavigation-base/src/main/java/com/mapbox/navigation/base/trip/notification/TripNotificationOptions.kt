package com.mapbox.navigation.base.trip.notification

import com.mapbox.navigation.base.TimeFormat
import com.mapbox.navigation.base.formatter.DistanceFormatter

data class TripNotificationOptions(
    @TimeFormat.Type val timeFormatType: Int,
    val distanceFormatter: DistanceFormatter?
) {
    class Builder {
        private var timeFormatType: Int = TimeFormat.NONE_SPECIFIED
        private var distanceFormatter: DistanceFormatter? = null

        /**
         * Defines [Mapbox Access Token](https://docs.mapbox.com/help/glossary/access-token/)
         */
        fun timeFormatType(@TimeFormat.Type timeFormatType: Int) =
            apply { this.timeFormatType = timeFormatType }

        /**
         * TODO
         */
        fun distanceFormatter(distanceFormatter: DistanceFormatter) =
            apply { this.distanceFormatter = distanceFormatter }

        fun build() = TripNotificationOptions(
            timeFormatType,
            distanceFormatter
        )
    }
}
