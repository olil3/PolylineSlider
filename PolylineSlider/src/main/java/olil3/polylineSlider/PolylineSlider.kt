package olil3.polylineSlider

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs

class PolylineSlider : ConstraintLayout {
    private var mPolylineSliderGraph: PolylineSliderGraph
    private var mXAxis: xAxis
    private var mSliderLinearLayout: LinearLayout
    private var mXAxisLinearLayout: LinearLayout
    private var mNumberOfDataPoints = 0
    private var sliderAlphaValue: Int = 0
    private var mThumbColor: Int = 0
    private var isBaseUIInitialized = false
    private var viewWidth = 0
    private var mGradientColor: Int = 0

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        View.inflate(context, R.layout.polyline_slider, this)
        mSliderLinearLayout = findViewById(R.id.slider_graph)
        mXAxisLinearLayout = findViewById(R.id.slider_x_axis)

        /* As this layout acts as a housing for multiple subviews, disable drawing to avoid misuse of resources. */
        setWillNotDraw(true)

        if (attributeSet != null) {
            val attributes = mContext.obtainStyledAttributes(
                attributeSet,
                R.styleable.PolylineSlider,
                0,
                0
            )
            try {
                mNumberOfDataPoints =
                    attributes.getInt(R.styleable.PolylineSlider_number_of_data_points, 1)
                sliderAlphaValue =
                    attributes.getInt(R.styleable.PolylineSlider_is_slider_track_visible, 0)
                mThumbColor =
                    attributes.getInt(R.styleable.PolylineSlider_thumb_color, Color.MAGENTA)
                mGradientColor =
                    attributes.getColor(
                        R.styleable.PolylineSlider_gradient_color,
                        Color.rgb(238, 130, 238)
                    )
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
            mContext,
            mNumberOfDataPoints,
            sliderAlphaValue,
            mThumbColor,
            mGradientColor
        )
        mPolylineSliderGraph.id = View.generateViewId()
        val mSliderLinearLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        mSliderLinearLayout.addView(mPolylineSliderGraph, mSliderLinearLayoutParams)
        mXAxis = xAxis(mContext, mNumberOfDataPoints, "Hrs")

        val xAxisLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mXAxisLinearLayout.addView(mXAxis, xAxisLayoutParams)

        mXAxis.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            mPolylineSliderGraph.scrollX = scrollX
        }
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)

    constructor(mContext: Context) : this(mContext, null, 0)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!isBaseUIInitialized) {
            mPolylineSliderGraph.viewHeight = abs(t - b)
            viewWidth = abs(r - l)

            mPolylineSliderGraph.viewWidth = viewWidth

            mPolylineSliderGraph.mSliderSpacingWidth = getSliderSpacing(mNumberOfDataPoints)
            mPolylineSliderGraph.initializeBaseUI()

            mXAxis.mSliderSpacing = mPolylineSliderGraph.mSliderSpacingWidth
            mXAxis.initializeBaseUi()
            isBaseUIInitialized = true
            invalidate()
        }
    }

    private fun getSliderSpacing(numberOfSliders: Int): Int {
        val minimumNumberOfSlidersInFocus = 8
        return if (numberOfSliders >= minimumNumberOfSlidersInFocus) {
            (viewWidth / minimumNumberOfSlidersInFocus)
        } else {
            (viewWidth / numberOfSliders)
        }
    }

}