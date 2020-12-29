package com.mapbox.navigation.ui.route

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.mapbox.navigation.ui.R
import com.mapbox.navigation.ui.internal.route.RouteConstants.DEFAULT_ROUTE_SOURCES_MAX_ZOOM
import com.mapbox.navigation.ui.internal.route.RouteConstants.DEFAULT_ROUTE_SOURCES_TOLERANCE
import com.mapbox.navigation.ui.internal.route.RouteConstants.LAYER_ABOVE_UPCOMING_MANEUVER_ARROW

class RouteArrowOptions private constructor(
    val sourceMaxZoom: Int,
    val sourceTolerance: Float,
    @ColorInt val arrowColor: Int,
    @ColorInt val arrowBorderColor: Int,
    val arrowHeadIcon: Drawable,
    val arrowHeadIconBorder: Drawable,
    val aboveLayerId: String
){

    fun toBuilder(context: Context, @StyleRes styleRes: Int): Builder {
        return Builder(context, styleRes)
    }

    class Builder(private val context: Context, @StyleRes private val styleRes: Int) {
        var sourceMaxZoom: Int? = null
        var sourceTolerance: Float? = null
        @ColorInt var arrowColor: Int? = null
        @ColorInt var arrowBorderColor: Int? = null
        var arrowHeadIcon: Drawable? = null
        var arrowHeadIconBorder: Drawable? = null
        var aboveLayerId: String? = null

        fun sourceMaxZoom(sourceMaxZoom: Int): Builder = apply {
            this.sourceMaxZoom = sourceMaxZoom
        }

        fun sourceTolerance(sourceTolerance: Float): Builder = apply {
            this.sourceTolerance = sourceTolerance
        }

        fun arrowColor(@ColorInt color: Int) = apply {
            this.arrowColor = color
        }

        fun arrowBorderColor(@ColorInt color: Int) = apply {
            this.arrowBorderColor = color
        }

        fun arrowHeadIcon(icon: Drawable) = apply {
            this.arrowHeadIcon = icon
        }

        fun arrowHeadIconBorder(icon: Drawable) = apply {
            this.arrowHeadIconBorder = icon
        }

        fun aboveLayerId(layerId: String) = apply {
            this.aboveLayerId = layerId
        }

        fun build(): RouteArrowOptions {
            val typedArray: TypedArray =
                context.obtainStyledAttributes(styleRes, R.styleable.MapboxStyleNavigationMapRoute)

            val finalArrowColor:Int = arrowColor ?: typedArray.getColor(
                R.styleable.MapboxStyleNavigationMapRoute_upcomingManeuverArrowColor,
                ContextCompat.getColor(
                    context,
                    R.color.mapbox_navigation_route_upcoming_maneuver_arrow_color
                )
            )

            val finalBorderColor: Int = arrowBorderColor ?: typedArray.getColor(
                R.styleable.MapboxStyleNavigationMapRoute_upcomingManeuverArrowBorderColor,
                ContextCompat.getColor(
                    context,
                    R.color.mapbox_navigation_route_upcoming_maneuver_arrow_border_color
                )
            )
            typedArray.recycle()

            val finalSourceMaxZoom: Int = sourceMaxZoom ?: DEFAULT_ROUTE_SOURCES_MAX_ZOOM
            val finalSourceTolerance: Float = sourceTolerance ?: DEFAULT_ROUTE_SOURCES_TOLERANCE
            val finalArrowHeadIcon: Drawable = arrowHeadIcon ?: AppCompatResources.getDrawable(
                context,
                R.drawable.mapbox_ic_arrow_head
            )!!
            val finalArrowHeadBorder: Drawable = arrowHeadIconBorder ?:
                AppCompatResources.getDrawable(context, R.drawable.mapbox_ic_arrow_head_casing)!!

            val finalLayerId: String = aboveLayerId ?: LAYER_ABOVE_UPCOMING_MANEUVER_ARROW

            return RouteArrowOptions(
                finalSourceMaxZoom,
                finalSourceTolerance,
                finalArrowColor,
                finalBorderColor,
                finalArrowHeadIcon,
                finalArrowHeadBorder,
                finalLayerId
            )
        }
    }
}
