package olil3.polylineSlider

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import olil3.polylineSlider.uiComponents.Axis
import olil3.polylineSlider.uiComponents.PolylineSliderGraph

internal const val X_AXIS_TYPE = 1000
internal const val Y_AXIS_TYPE = 2000

class PolylineSlider : ConstraintLayout { // Todo: Add save state functionality
    private lateinit var mSlider: PolylineSliderGraph
    private lateinit var mXAxis: Axis
    private lateinit var mYAxis: Axis
    private var mNumberOfDataPoints = 0
    private var sliderAlphaValue: Int = 0
    private var mThumbColor: Int = 0
    private var mGradientColor: Int = 0
    private var mSliderSpacing: Int = 0
    private var isUIInitialized = false
    private var mTextViewID: IntArray
    private var mSliderID: IntArray
    private var mPercentageViewID: IntArray

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        View.inflate(context, R.layout.polyline_slider, this)

        /* As this layout acts as a housing for multiple subviews, disable drawing to avoid misuse of resources. */
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
        mTextViewID = IntArray(mNumberOfDataPoints)
        mSliderID = IntArray(mNumberOfDataPoints)
        mPercentageViewID = IntArray(mNumberOfDataPoints)
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0) {
        // Do nothing.
    }

    constructor(mContext: Context) : this(mContext, null, 0) {
        // Do nothing.
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!isUIInitialized || changed) {
            mSliderSpacing = getSliderSpacing(mNumberOfDataPoints)
            objectInit()
            isUIInitialized = true
        }
    }

    private fun objectInit() {
        val mThumbColorFilter = PorterDuffColorFilter(mThumbColor, PorterDuff.Mode.SRC_ATOP)
        val mSliderColorFilter = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)

        mSlider = findViewById(R.id.polyline_slider_graph)
        mSlider
            .initParams(
                mNumberOfDataPoints,
                mGradientColor,
                mSliderSpacing,
                this,
                sliderAlphaValue,
                mThumbColorFilter,
                mSliderColorFilter,
                mSliderID
            )

        mYAxis = findViewById(R.id.polyline_y_axis)
        mYAxis.setNumberOfItems(mNumberOfDataPoints)
        mYAxis.setItemSpacing(mSliderSpacing)
        mYAxis.setUnit("%")
        mYAxis.setItemViewIDArray(mPercentageViewID)
        mYAxis.setAdapter(Y_AXIS_TYPE, 50)
        mYAxis.setLayout()

        mXAxis = findViewById(R.id.polyline_x_axis)
        mXAxis.setNumberOfItems(mNumberOfDataPoints)
        mXAxis.setItemSpacing(mSliderSpacing)
        mXAxis.setUnit("Hrs")
        mXAxis.setItemViewIDArray(mTextViewID)
        mXAxis.setAdapter(X_AXIS_TYPE, 0)
        mXAxis.setLayout()

        mSlider.setAdapter()
        mSlider.setLayoutParams()
        mSlider.post {
            mSlider.initiatePostSequence()
        }
    }

    internal fun updateText(position: Int, code: Int) {
        when (code) {
            0 -> {
                try {
                    findViewById<TextView>(mTextViewID[position]).typeface = Typeface.DEFAULT
                } catch (e: Exception) {
                    Log.e("Exception Class", "${e.message}")
                }
            }
            1 -> {
                try {
                    findViewById<TextView>(mTextViewID[position]).typeface = Typeface.DEFAULT_BOLD
                } catch (e: Exception) {
                    Log.e("Exception Class", "${e.message}")
                }
            }
            else -> {
                Log.e("Wut dis", "$code")
            }
        }
    }

    private fun getSliderSpacing(numberOfPoints: Int): Int {
        return if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val numberOfSlidersInView = 5
            if (numberOfPoints >= numberOfSlidersInView) {
                measuredWidth / numberOfSlidersInView
            } else {
                measuredWidth / numberOfPoints
            }
        } else {
            measuredWidth / numberOfPoints
        }
    }

    internal fun performViewScroll(scrollingValue: Int) {
        mXAxis.scrollBy(scrollingValue, 0)
        mYAxis.scrollBy(scrollingValue, 0)
    }

    internal fun changeYAxisProgress(position: Int, progress: Int) {
        mYAxis.changeYAxisProgress(position, progress)
    }
}
