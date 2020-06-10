package olil3.polylineSlider

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper

class PolylineSlider : RelativeLayout {
    private lateinit var mSliderWrapperViewIDs: IntArray
    private var mNumberOfDataPoints: Int = 0
    private var mThumbColor: Int = 0
    private lateinit var mSliderThumbColor: PorterDuffColorFilter
    private var sliderAlphaValue: Int? = 0

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        objectInit(mContext, attributeSet, defStyleAttr)
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)
    constructor(mContext: Context) : this(mContext, null, 0)

    private fun objectInit(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) {
        View.inflate(mContext, R.layout.polyline_slider, this)

        if (attributeSet != null) {
            val attributes = mContext.obtainStyledAttributes(
                attributeSet,
                R.styleable.PolylineSlider,
                defStyleAttr,
                0
            )
            try {
                mNumberOfDataPoints =
                    attributes.getInt(R.styleable.PolylineSlider_number_of_data_points, 1)
                sliderAlphaValue =
                    attributes.getInt(R.styleable.PolylineSlider_is_slider_track_visible, 0)
                mThumbColor =
                    attributes.getInt(R.styleable.PolylineSlider_thumb_color, Color.MAGENTA)
            } catch (error: Exception) {
                Log.e("PolylineSlider init err", error.message!!)
                throw error
            } finally {
                attributes.recycle()
            }

            if (mNumberOfDataPoints < 1) {
                throw IllegalArgumentException(mContext.resources.getString(R.string.invalid_number_of_data_points))
            }

            mSliderThumbColor = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)
            mSliderWrapperViewIDs = IntArray(mNumberOfDataPoints)
        }
    }

    private fun initializeBaseUI() {
        for (sliderWrapper in 0 until mNumberOfDataPoints) {
            val mSlider = VerticalSeekBar(context)
            val mSliderWrapper = VerticalSeekBarWrapper(context)

            mSliderWrapper.id = View.generateViewId()
            mSliderWrapperViewIDs[sliderWrapper] = mSliderWrapper.id
            mSliderWrapper.addView(mSlider)

            mSlider.rotationAngle = VerticalSeekBar.ROTATION_ANGLE_CW_270
            mSlider.max = 100
            mSlider.progress = 50
            mSlider.alpha = sliderAlphaValue!!.toFloat()
            mSlider.thumb.colorFilter = mSliderThumbColor
            mSlider.progressDrawable.colorFilter = mSliderThumbColor

        }
    }
}