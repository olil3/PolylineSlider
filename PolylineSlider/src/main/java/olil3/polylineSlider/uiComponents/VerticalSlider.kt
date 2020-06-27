package olil3.polylineSlider.uiComponents

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.SeekBar
import olil3.polylineSlider.utils.EPointF

internal class VerticalSlider : RelativeLayout {
    private var mSlider: SeekBar = SeekBar(context)
    var thumbAlpha: Int
        get() {
            return mSlider.thumb.alpha
        }
        set(value) {
            mSlider.thumb.alpha = value
        }

    var thumbColor: ColorFilter?
        get() {
            return mSlider.thumb.colorFilter
        }
        set(value) {
            mSlider.thumb.colorFilter = value
        }

    var sliderAlpha: Int
        get() {
            return mSlider.progressDrawable.alpha
        }

        set(value) {
            mSlider.progressDrawable.alpha = value
        }

    var sliderColor: ColorFilter?
        get() {
            return mSlider.progressDrawable.colorFilter
        }
        set(value) {
            mSlider.progressDrawable.colorFilter = value
        }

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(mContext, attributeSet, defStyleAttr) {
        val mSeekBarRotator = SeekBarRotator(context)
        mSeekBarRotator.addView(mSlider, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        this.addView(mSeekBarRotator, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0) {
        // Do nothing
    }
    constructor(mContext: Context) : this(mContext, null, 0) {
        // Do nothing
    }

    fun getSeekBarObject(): SeekBar {
        return mSlider
    }

    fun getSliderCoordinates(): EPointF {
        val seekBarThumbBounds = mSlider.thumb.bounds
        val xPos: Float =
            seekBarThumbBounds.exactCenterY()
        val yPos: Float =
            this.bottom - seekBarThumbBounds.exactCenterX() - (seekBarThumbBounds.height() * 0.4f)
        return EPointF(xPos, yPos)
    }
}
