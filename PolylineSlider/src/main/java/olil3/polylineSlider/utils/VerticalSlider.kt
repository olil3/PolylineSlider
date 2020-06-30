package olil3.polylineSlider.utils

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import android.view.ViewGroup

class VerticalSlider : ViewGroup {
    private var mVerticalSeekBarWrapper: VerticalSeekBarWrapper = VerticalSeekBarWrapper(context)
    private var mVerticalSeekBar: VerticalSeekBar = VerticalSeekBar(context)

    var thumbAlpha: Int
        get() {
            return mVerticalSeekBar.thumb.alpha
        }
        set(value) {
            mVerticalSeekBar.thumb.alpha = value
        }

    var thumbColor: ColorFilter?
        get() {
            return mVerticalSeekBar.thumb.colorFilter
        }
        set(value) {
            mVerticalSeekBar.thumb.colorFilter = value
        }

    var sliderAlpha: Int
        get() {
            return mVerticalSeekBar.progressDrawable.alpha
        }
        set(value) {
            mVerticalSeekBar.progressDrawable.alpha = value
        }

    var sliderColor: ColorFilter?
        get() {
            return mVerticalSeekBar.progressDrawable.colorFilter
        }
        set(value) {
            mVerticalSeekBar.progressDrawable.colorFilter = value
        }

    var sliderProgress: Int
        get() {
            return mVerticalSeekBar.progress
        }
        set(value) {
            mVerticalSeekBar.progress = value
        }

    var sliderMax: Int
        get() {
            return mVerticalSeekBar.max
        }
        set(value) {
            mVerticalSeekBar.max = value
        }

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        mVerticalSeekBar.rotationAngle = VerticalSeekBar.ROTATION_ANGLE_CW_270

        mVerticalSeekBarWrapper.addView(
            mVerticalSeekBar,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        )
        this.addView(
            mVerticalSeekBarWrapper,
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        )
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0) {
        // Do nothing
    }

    constructor(mContext: Context) : this(mContext, null, 0) {
        // Do nothing
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (changed) {
            mVerticalSeekBarWrapper.layout(paddingLeft, 0, measuredWidth, measuredHeight)
        }
    }

    fun getSliderCoordinates(): EPointF {
        val seekBarThumbBounds = mVerticalSeekBar.thumb.bounds
        val xPos: Float =
            mVerticalSeekBarWrapper.left + seekBarThumbBounds.exactCenterY() + ((mVerticalSeekBarWrapper.width - (mVerticalSeekBar.paddingLeft * 1.1f)) / 2)
        val yPos: Float =
            mVerticalSeekBarWrapper.bottom - seekBarThumbBounds.exactCenterX() - (seekBarThumbBounds.height() * 0.4f)

        return EPointF(xPos, yPos)
    }

    fun getSeekBarObject(): VerticalSeekBar {
        return mVerticalSeekBar
    }
}
