package olil3.polylineSlider

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import olil3.polylineSlider.utils.PolylineSliderGraph

class PolylineSlider : RelativeLayout {
    private var mPolylineSliderGraph: PolylineSliderGraph

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        /* As this layout acts as a housing for multiple subviews, disable drawing to avoid misuse of resources. */
        setWillNotDraw(false)

        var mNumberOfDataPoints = 2
        var sliderAlphaValue: Int? = 0
        var mThumbColor: Int = Color.MAGENTA

        if (attributeSet != null) {
            val attributes = mContext.obtainStyledAttributes(
                attributeSet,
                R.styleable.PolylineSlider,
                0,
                0
            )
            try {
                mNumberOfDataPoints =
                    attributes.getInt(R.styleable.PolylineSlider_number_of_data_points, 2)
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

        }

        mPolylineSliderGraph = PolylineSliderGraph(
            mNumberOfDataPoints,
            sliderAlphaValue,
            mThumbColor,
            mContext
        )
        this.addView(mPolylineSliderGraph)
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)
    constructor(mContext: Context) : this(mContext, null, 0)
}