package com.mapbox.navigation.ui.voice.soundbutton.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.navigation.ui.base.MapboxView
import com.mapbox.navigation.ui.base.internal.utils.MultiOnClickListener
import com.mapbox.navigation.ui.base.model.soundbutton.SoundButtonState
import com.mapbox.navigation.ui.voice.R

class SoundButton : ConstraintLayout, MapboxView<SoundButtonState> {

    private lateinit var soundFab: FloatingActionButton
    private lateinit var soundChipText: TextView
    private var fadeInSlowOut: AnimationSet? = null
    private var isMuted = false
    private var multiOnClickListener: MultiOnClickListener? = MultiOnClickListener()

    private var primaryColor = 0
    private var secondaryColor = 0

    companion object {
        private const val ALPHA_VALUE_ZERO = 0f
        private const val ALPHA_VALUE_ONE = 1f
        private const val ANIMATION_DURATION_THREE_HUNDRED_MILLIS: Long = 300
        private const val ANIMATION_DURATION_ONE_THOUSAND_MILLIS: Long = 1000
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttributes(attrs)
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initAttributes(attrs)
        init(context)
    }

    private fun initAttributes(attributeSet: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attributeSet, R.styleable.MapboxStyleSoundButton)
        primaryColor = ContextCompat.getColor(
            context,
            typedArray.getResourceId(
                R.styleable.MapboxStyleSoundButton_soundButtonPrimaryColor,
                R.color.mapbox_sound_button_primary
            )
        )
        secondaryColor = ContextCompat.getColor(
            context,
            typedArray.getResourceId(
                R.styleable.MapboxStyleSoundButton_soundButtonSecondaryColor,
                R.color.mapbox_sound_button_secondary
            )
        )
        typedArray.recycle()
    }

    private fun init(context: Context) {
        inflate(context, R.layout.mapbox_button_sound, this)
    }

    override fun render(state: SoundButtonState) {
        when (state) {
            is SoundButtonState.SoundEnabled -> {
                if (state.enable) {
                    unmute()
                } else {
                    mute()
                }
            }
        }
    }

    /**
     * Adds an onClickListener to the button
     *
     * @param onClickListener to add
     */
    fun addOnClickListener(onClickListener: OnClickListener) {
        multiOnClickListener?.addListener(onClickListener)
    }

    /**
     * Removes an onClickListener from the button
     *
     * @param onClickListener to remove
     */
    fun removeOnClickListener(onClickListener: OnClickListener) {
        multiOnClickListener?.removeListener(onClickListener)
    }

    fun updateStyle(@StyleRes styleRes: Int) {
        val typedArray =
            context.obtainStyledAttributes(styleRes, R.styleable.MapboxStyleSoundButton)
        primaryColor = ContextCompat.getColor(
            context,
            typedArray.getResourceId(
                R.styleable.MapboxStyleSoundButton_soundButtonPrimaryColor,
                R.color.mapbox_sound_button_primary
            )
        )
        secondaryColor = ContextCompat.getColor(
            context,
            typedArray.getResourceId(
                R.styleable.MapboxStyleSoundButton_soundButtonSecondaryColor,
                R.color.mapbox_sound_button_secondary
            )
        )
        typedArray.recycle()
        applyAttributes()
    }

    /**
     * Will traffic_toggle_activity the view between muted and unmuted states.
     *
     * @return boolean true if muted, false if not
     */
    private fun toggleMute(): Boolean =
        if (isMuted) {
            unmute()
        } else {
            mute()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        bind()
        applyAttributes()
        initializeAnimation()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupOnClickListeners()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearListeners()
    }

    private fun setupOnClickListeners() {
        soundFab.setOnClickListener(multiOnClickListener)
    }

    private fun clearListeners() {
        multiOnClickListener?.clearListeners()
        soundFab.setOnClickListener(null)
        setOnClickListener(null)
    }

    private fun initializeAnimation() {
        val fadeIn: Animation = AlphaAnimation(ALPHA_VALUE_ZERO, ALPHA_VALUE_ONE)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = ANIMATION_DURATION_THREE_HUNDRED_MILLIS
        val fadeOut: Animation = AlphaAnimation(ALPHA_VALUE_ONE, ALPHA_VALUE_ZERO)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = ANIMATION_DURATION_ONE_THOUSAND_MILLIS
        fadeOut.duration = ANIMATION_DURATION_ONE_THOUSAND_MILLIS
        fadeInSlowOut = AnimationSet(false).also {
            it.addAnimation(fadeIn)
            it.addAnimation(fadeOut)
        }
    }

    private fun applyAttributes() {
        val soundChipBackground = DrawableCompat.wrap(soundChipText?.background).mutate()
        DrawableCompat.setTint(soundChipBackground, primaryColor)
        soundChipText.setTextColor(secondaryColor)
        soundFab.backgroundTintList = ColorStateList.valueOf(primaryColor)
        soundFab.supportImageTintList = ColorStateList.valueOf(secondaryColor)
    }

    private fun bind() {
        soundFab = findViewById(R.id.soundFab)
        soundChipText = findViewById(R.id.soundText)
    }

    /**
     * Sets up mute UI event.
     *
     * Shows chip with "Muted" text.
     * Changes sound [FloatingActionButton]
     * [Drawable] to denote sound is off.
     *
     * Sets private state variable to true (muted)
     *
     * @return true, view is in muted state
     */
    private fun mute(): Boolean {
        isMuted = true
        setSoundChipText(context.getString(R.string.mapbox_muted))
        showSoundChip()
        soundFabOff()
        return isMuted
    }

    /**
     * Sets up unmuted UI event.
     *
     *
     * Shows chip with "Unmuted" text.
     * Changes sound [FloatingActionButton]
     * [Drawable] to denote sound is on.
     *
     *
     * Sets private state variable to false (unmuted)
     *
     * @return false, view is in unmuted state
     */
    private fun unmute(): Boolean {
        isMuted = false
        setSoundChipText(context.getString(R.string.mapbox_unmuted))
        showSoundChip()
        soundFabOn()
        return isMuted
    }

    /**
     * Sets [TextView] inside of chip view.
     *
     * @param text to be displayed in chip view ("Muted"/"Umuted")
     */
    private fun setSoundChipText(text: String) {
        soundChipText.text = text
    }

    /**
     * Shows and then hides the sound chip using [AnimationSet]
     */
    private fun showSoundChip() {
        soundChipText.startAnimation(fadeInSlowOut)
    }

    /**
     * Changes sound [FloatingActionButton]
     * [Drawable] to denote sound is on.
     */
    private fun soundFabOn() {
        soundFab.setImageResource(R.drawable.mapbox_ic_sound_on)
    }

    /**
     * Changes sound [FloatingActionButton]
     * [Drawable] to denote sound is off.
     */
    private fun soundFabOff() {
        soundFab.setImageResource(R.drawable.mapbox_ic_sound_off)
    }
}
