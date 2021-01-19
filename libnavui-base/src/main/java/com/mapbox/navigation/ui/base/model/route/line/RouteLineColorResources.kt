package com.mapbox.navigation.ui.base.model.route.line

import androidx.annotation.ColorInt
import com.mapbox.navigation.ui.base.internal.route.RouteConstants

/**
 * Contains colors an other values used to determine the appearance of the route line.
 *
 * @param routeLineTraveledColor the color of the section of route line behind the puck
 * representing the section of the route traveled
 * @param routeLineTraveledCasingColor the color of the casing section of route line behind the
 * puck representing the section of the route traveled. By default the casing line is beneath
 * the route line and gives the appearance of a border
 * @param routeUnknownTrafficColor the color used for representing unknown traffic congestion
 * @param routeDefaultColor the default color of the route line
 * @param routeLowCongestionColor the color used for representing low traffic congestion
 * @param routeModerateColor the color used for representing moderate traffic congestion
 * @param routeHeavyColor the color used for representing heavy traffic congestion
 * @param routeSevereColor the color used for representing severe traffic congestion
 * @param routeCasingColor the color used for the route casing line which is positioned below
 * the route line giving the line the appearance of a boarder
 * @param alternativeRouteUnknownTrafficColor the color used for representing unknown traffic
 * congestion on alternative routes
 * @param alternativeRouteDefaultColor the default color used for alternative route lines
 * @param alternativeRouteLowColor the color used for representing low traffic congestion on
 * alternative routes
 * @param alternativeRouteModerateColor the color used for representing moderate traffic congestion
 * on alternative routes
 * @param alternativeRouteHeavyColor the color used for representing heavy traffic congestion on
 * alternative routes
 * @param alternativeRouteSevereColor the color used for representing severe traffic congestion
 * on alternative routes
 * @param alternativeRouteCasingColor the color used for the alternative route casing line(s) which
 * is positioned below the route line giving the line the appearance of a boarder
 */
class RouteLineColorResources private constructor(
    @ColorInt val routeLineTraveledColor: Int,
    @ColorInt val routeLineTraveledCasingColor: Int,
    @ColorInt val routeUnknownTrafficColor: Int,
    @ColorInt val routeDefaultColor: Int,
    @ColorInt val routeLowCongestionColor: Int,
    @ColorInt val routeModerateColor: Int,
    @ColorInt val routeHeavyColor: Int,
    @ColorInt val routeSevereColor: Int,
    @ColorInt val routeCasingColor: Int,
    @ColorInt val alternativeRouteUnknownTrafficColor: Int,
    @ColorInt val alternativeRouteDefaultColor: Int,
    @ColorInt val alternativeRouteLowColor: Int,
    @ColorInt val alternativeRouteModerateColor: Int,
    @ColorInt val alternativeRouteHeavyColor: Int,
    @ColorInt val alternativeRouteSevereColor: Int,
    @ColorInt val alternativeRouteCasingColor: Int
) {

    /**
     * @return builder matching the one used to create this instance
     */
    fun toBuilder(): Builder {
        return Builder()
            .routeLineTraveledColor(routeLineTraveledColor)
            .routeLineTraveledCasingColor(routeLineTraveledCasingColor)
            .routeUnknownTrafficColor(routeUnknownTrafficColor)
            .routeDefaultColor(routeDefaultColor)
            .routeLowCongestionColor(routeLowCongestionColor)
            .routeModerateColor(routeModerateColor)
            .routeHeavyColor(routeHeavyColor)
            .routeSevereColor(routeSevereColor)
            .routeCasingColor(routeCasingColor)
            .alternativeRouteUnknownTrafficColor(alternativeRouteUnknownTrafficColor)
            .alternativeRouteDefaultColor(alternativeRouteDefaultColor)
            .alternativeRouteLowColor(alternativeRouteLowColor)
            .alternativeRouteModerateColor(alternativeRouteModerateColor)
            .alternativeRouteHeavyColor(alternativeRouteHeavyColor)
            .alternativeRouteSevereColor(alternativeRouteSevereColor)
            .alternativeRouteCasingColor(alternativeRouteCasingColor)
    }

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "RouteLineResources(" +
            "routeLineTraveledColor=$routeLineTraveledColor)" +
            "routeLineTraveledCasingColor=$routeLineTraveledCasingColor" +
            "routeUnknownTrafficColor=$routeUnknownTrafficColor" +
            "routeDefaultColor=$routeDefaultColor" +
            "routeLowCongestionColor=$routeLowCongestionColor" +
            "routeModerateColor=$routeModerateColor" +
            "routeHeavyColor=$routeHeavyColor" +
            "routeSevereColor=$routeSevereColor" +
            "routeCasingColor=$routeCasingColor" +
            "alternativeRouteUnknownTrafficColor=$alternativeRouteUnknownTrafficColor" +
            "alternativeRouteDefaultColor=$alternativeRouteDefaultColor" +
            "alternativeRouteLowColor=$alternativeRouteLowColor" +
            "alternativeRouteModerateColor=$alternativeRouteModerateColor" +
            "alternativeRouteHeavyColor=$alternativeRouteHeavyColor" +
            "alternativeRouteSevereColor=$alternativeRouteSevereColor" +
            "alternativeRouteCasingColor=$alternativeRouteCasingColor"
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RouteLineColorResources

        if (routeLineTraveledColor != other.routeLineTraveledColor) return false
        if (routeLineTraveledCasingColor != other.routeLineTraveledCasingColor) return false
        if (routeUnknownTrafficColor != other.routeUnknownTrafficColor) return false
        if (routeDefaultColor != other.routeDefaultColor) return false
        if (routeLowCongestionColor != other.routeLowCongestionColor) return false
        if (routeModerateColor != other.routeModerateColor) return false
        if (routeHeavyColor != other.routeHeavyColor) return false
        if (routeSevereColor != other.routeSevereColor) return false
        if (routeCasingColor != other.routeCasingColor) return false
        if (alternativeRouteDefaultColor != other.alternativeRouteDefaultColor) return false
        if (alternativeRouteLowColor != other.alternativeRouteLowColor) return false
        if (alternativeRouteModerateColor != other.alternativeRouteModerateColor) return false
        if (alternativeRouteHeavyColor != other.alternativeRouteHeavyColor) return false
        if (alternativeRouteSevereColor != other.alternativeRouteSevereColor) return false
        if (alternativeRouteCasingColor != other.alternativeRouteCasingColor) return false
        if (alternativeRouteUnknownTrafficColor != other.alternativeRouteUnknownTrafficColor)
            return false

        return true
    }

    /**
     * Returns a hash code value for the object.
     */
    override fun hashCode(): Int {
        var result = routeLineTraveledColor
        result = 31 * result + routeLineTraveledCasingColor
        result = 31 * result + routeUnknownTrafficColor
        result = 31 * result + routeDefaultColor
        result = 31 * result + routeLowCongestionColor
        result = 31 * result + routeModerateColor
        result = 31 * result + routeHeavyColor
        result = 31 * result + routeSevereColor
        result = 31 * result + routeCasingColor
        result = 31 * result + alternativeRouteUnknownTrafficColor
        result = 31 * result + alternativeRouteDefaultColor
        result = 31 * result + alternativeRouteLowColor
        result = 31 * result + alternativeRouteModerateColor
        result = 31 * result + alternativeRouteHeavyColor
        result = 31 * result + alternativeRouteSevereColor
        result = 31 * result + alternativeRouteCasingColor
        return result
    }

    /**
     * A builder for instantiating the RouteLineResources class
     */
    class Builder {
        private var routeLineTraveledColor: Int = RouteConstants.ROUTE_LINE_TRAVELED_COLOR
        private var routeLineTraveledCasingColor: Int = RouteConstants.ROUTE_LINE_TRAVELED_CASING_COLOR
        private var routeUnknownTrafficColor: Int = RouteConstants.ROUTE_UNKNOWN_TRAFFIC_COLOR
        private var routeDefaultColor: Int = RouteConstants.ROUTE_DEFAULT_COLOR
        private var routeLowCongestionColor: Int = RouteConstants.ROUTE_LOW_TRAFFIC_COLOR
        private var routeModerateColor: Int = RouteConstants.ROUTE_MODERATE_TRAFFIC_COLOR
        private var routeHeavyColor: Int = RouteConstants.ROUTE_HEAVY_TRAFFIC_COLOR
        private var routeSevereColor: Int = RouteConstants.ROUTE_SEVERE_TRAFFIC_COLOR
        private var routeCasingColor: Int = RouteConstants.ROUTE_CASING_COLOR
        private var alternativeRouteUnknownTrafficColor: Int = RouteConstants.ALTERNATE_ROUTE_UNKNOWN_TRAFFIC_COLOR
        private var alternativeRouteDefaultColor: Int = RouteConstants.ALTERNATE_ROUTE_DEFAULT_COLOR
        private var alternativeRouteLowColor: Int = RouteConstants.ALTERNATE_ROUTE_LOW_TRAFFIC_COLOR
        private var alternativeRouteModerateColor: Int = RouteConstants.ALTERNATE_ROUTE_MODERATE_TRAFFIC_COLOR
        private var alternativeRouteHeavyColor: Int = RouteConstants.ALTERNATE_ROUTE_HEAVY_TRAFFIC_COLOR
        private var alternativeRouteSevereColor: Int = RouteConstants.ALTERNATE_ROUTE_SEVERE_TRAFFIC_COLOR
        private var alternativeRouteCasingColor: Int = RouteConstants.ALTERNATE_ROUTE_CASING_COLOR

        /**
         * The color of the section of route line behind the puck representing the section
         * of the route traveled.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeLineTraveledColor(@ColorInt color: Int): Builder =
            apply { this.routeLineTraveledColor = color }

        /**
         * The color of the casing section of route line behind the puck representing the section
         * of the route traveled. By default the casing line is beneath the route line and
         * gives the appearance of a border.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeLineTraveledCasingColor(@ColorInt color: Int): Builder =
            apply { this.routeLineTraveledCasingColor = color }

        /**
         * The color used for representing unknown traffic congestion.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeUnknownTrafficColor(@ColorInt color: Int): Builder =
            apply { this.routeUnknownTrafficColor = color }

        /**
         * The default color of the route line
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeDefaultColor(@ColorInt color: Int): Builder =
            apply { this.routeDefaultColor = color }

        /**
         * The color used for representing low traffic congestion.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeLowCongestionColor(@ColorInt color: Int): Builder =
            apply { this.routeLowCongestionColor = color }

        /**
         * The color used for representing moderate traffic congestion.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeModerateColor(@ColorInt color: Int): Builder =
            apply { this.routeModerateColor = color }

        /**
         * The color used for representing heavy traffic congestion.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeHeavyColor(@ColorInt color: Int): Builder =
            apply { this.routeHeavyColor = color }

        /**
         * The color used for representing severe traffic congestion.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeSevereColor(@ColorInt color: Int): Builder =
            apply { this.routeSevereColor = color }

        /**
         * The color used for the route casing line which is positioned below the route line
         * giving the line the appearance of a boarder.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun routeCasingColor(@ColorInt color: Int): Builder =
            apply { this.routeCasingColor = color }

        /**
         * The color used for representing unknown traffic congestion on alternative routes.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun alternativeRouteUnknownTrafficColor(@ColorInt color: Int): Builder =
            apply { this.alternativeRouteUnknownTrafficColor = color }

        /**
         * The default color used for alternative route lines.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun alternativeRouteDefaultColor(@ColorInt color: Int): Builder =
            apply { this.alternativeRouteDefaultColor = color }

        /**
         * The color used for representing low traffic congestion on alternative routes.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun alternativeRouteLowColor(@ColorInt color: Int): Builder =
            apply { this.alternativeRouteLowColor = color }

        /**
         * The color used for representing moderate traffic congestion on alternative routes.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun alternativeRouteModerateColor(@ColorInt color: Int): Builder =
            apply { this.alternativeRouteModerateColor = color }

        /**
         * The color used for representing heavy traffic congestion on alternative routes.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun alternativeRouteHeavyColor(@ColorInt color: Int): Builder =
            apply { this.alternativeRouteHeavyColor = color }

        /**
         * The color used for representing severe traffic congestion on alternative routes.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun alternativeRouteSevereColor(@ColorInt color: Int): Builder =
            apply { this.alternativeRouteSevereColor = color }

        /**
         * The color used for the alternative route casing line(s) which is positioned below the route line
         * giving the line the appearance of a boarder.
         *
         * @param color the color to be used
         *
         * @return the builder
         */
        fun alternativeRouteCasingColor(@ColorInt color: Int): Builder =
            apply { this.alternativeRouteCasingColor = color }

        /**
         * Creates a instance of RouteLineResources
         *
         * @return the instance
         */
        fun build(): RouteLineColorResources {
            return RouteLineColorResources(
                routeLineTraveledColor,
                routeLineTraveledCasingColor,
                routeUnknownTrafficColor,
                routeDefaultColor,
                routeLowCongestionColor,
                routeModerateColor,
                routeHeavyColor,
                routeSevereColor,
                routeCasingColor,
                alternativeRouteUnknownTrafficColor,
                alternativeRouteDefaultColor,
                alternativeRouteLowColor,
                alternativeRouteModerateColor,
                alternativeRouteHeavyColor,
                alternativeRouteSevereColor,
                alternativeRouteCasingColor
            )
        }
    }
}

