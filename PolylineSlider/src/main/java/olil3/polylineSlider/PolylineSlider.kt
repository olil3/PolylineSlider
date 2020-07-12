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
import olil3.polylineSlider.utils.VerticalSeekBarWrapper

internal const val X_AXIS_TYPE = 1000
internal const val Y_AXIS_TYPE = 2000
internal const val ON_TOUCH_DOWN = 100
internal const val ON_TOUCH_UP = 200

class PolylineSlider : ConstraintLayout { // Todo: Add save state functionality
    private lateinit var mSliderComponent: PolylineSliderGraph
    private lateinit var mXAxis: Axis
    private lateinit var mYAxis: Axis
    private var mNumberOfDataPoints = 1
    private var sliderAlphaValue: Int = 0
    private var mThumbColor: Int = 0
    private var mGradientColor: Int = Color.rgb(238, 130, 238)
    private var mSliderSpacing: Int = 0
    private var isUIInitialized = false
    private var mXAxisTextViewIDs: IntArray
    private var mVerticalSliderIDs: IntArray
    private var mYAxisTextViewIDs: IntArray
    private var mXAxisUnit: String = ""
    private var mYAxisUnit: String = ""
    private var mYAxisMaxValue: Int = 100
    private var mYAxisMinValue: Int = 0
    private var mYAxisInitialValue: Int = 50

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
                        R.styleable.PolylineSlider_gradient_color, Color.rgb(238, 130, 238)
                    )
                mXAxisUnit = if (attributes.getString(R.styleable.PolylineSlider_x_axis_unit)
                        .toString() == "null"
                ) {
                    ""
                } else {
                    attributes.getString(R.styleable.PolylineSlider_x_axis_unit).toString()
                }

                mYAxisUnit = if (attributes.getString(R.styleable.PolylineSlider_y_axis_unit)
                        .toString() == "null"
                ) {
                    ""
                } else {
                    attributes.getString(R.styleable.PolylineSlider_y_axis_unit).toString()
                }
                mYAxisMaxValue = attributes.getInt(R.styleable.PolylineSlider_y_axis_max_value, 100)
                mYAxisMinValue = attributes.getInt(R.styleable.PolylineSlider_y_axis_min_value, 0)
                mYAxisInitialValue =
                    attributes.getInt(R.styleable.PolylineSlider_y_axis_initial_value, 50)
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
        mXAxisTextViewIDs = IntArray(mNumberOfDataPoints)
        mVerticalSliderIDs = IntArray(mNumberOfDataPoints)
        mYAxisTextViewIDs = IntArray(mNumberOfDataPoints)
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0) {
        // Do nothing.
    }

    constructor(mContext: Context) : this(mContext, null, 0) {
        // Do nothing.
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            if (!isUIInitialized) {
                mSliderSpacing = getSliderSpacing(mNumberOfDataPoints)
                objectInit()
                isUIInitialized = true
            } else {
                mSliderSpacing = getSliderSpacing(mNumberOfDataPoints)
                updateLayout()
            }
        }
    }

    private fun objectInit() {
        val mThumbColorFilter = PorterDuffColorFilter(mThumbColor, PorterDuff.Mode.SRC_ATOP)
        val mSliderColorFilter = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)

        mSliderComponent = findViewById(R.id.polyline_slider_graph)
        mYAxis = findViewById(R.id.polyline_y_axis)
        mYAxis.setNumberOfItems(mNumberOfDataPoints)
        mYAxis.setItemSpacing(mSliderSpacing)
        mYAxis.setUnit(mYAxisUnit)
        mYAxis.setItemViewIDArray(mYAxisTextViewIDs)
        mYAxis.setParent(this)
        mYAxis.setVerticalSliderViewIDArray(mVerticalSliderIDs)

        mXAxis = findViewById(R.id.polyline_x_axis)
        mXAxis.setNumberOfItems(mNumberOfDataPoints)
        mXAxis.setItemSpacing(mSliderSpacing)
        mXAxis.setUnit(mXAxisUnit)
        mXAxis.setItemViewIDArray(mXAxisTextViewIDs)

        mSliderComponent
            .initParams(
                mNumberOfDataPoints,
                mGradientColor,
                mSliderSpacing,
                this,
                sliderAlphaValue,
                mYAxisInitialValue,
                mThumbColorFilter,
                mSliderColorFilter,
                mVerticalSliderIDs
            )

        mSliderComponent.setAdapter()
        mSliderComponent.setLayoutParams()
        mSliderComponent.post {
            mSliderComponent.initiatePostSequence()
            mYAxis.setAdapter(Y_AXIS_TYPE)
            mYAxis.setLayout()

            mXAxis.setAdapter(X_AXIS_TYPE)
            mXAxis.setLayout()
        }
    }

    fun updateText(position: Int, code: Int) {
        when (code) {
            ON_TOUCH_UP -> {
                try {
                    findViewById<TextView>(mXAxisTextViewIDs[position]).typeface = Typeface.DEFAULT
                } catch (e: Exception) {
                    Log.e("Exception Class", "${e.message}")
                }
            }
            ON_TOUCH_DOWN -> {
                try {
                    findViewById<TextView>(mXAxisTextViewIDs[position]).typeface =
                        Typeface.DEFAULT_BOLD
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

    fun performViewScroll(scrollingValue: Int) {
        mXAxis.scrollBy(scrollingValue, 0)
        mYAxis.scrollBy(scrollingValue, 0)
    }

    fun changeYAxisProgress(position: Int, progress: Int) {
        mYAxis.changeYAxisProgress(position, progress)
    }

    private fun updateLayout() {
        mYAxis.updateLayout(mSliderSpacing)
        mXAxis.updateLayout(mSliderSpacing)
        mSliderComponent.updateLayout(mSliderSpacing)
    }

    fun getSliderProgressValue(position: Int): Int {
        val mVerticalSlider = findViewById<VerticalSeekBarWrapper>(mVerticalSliderIDs[position])
        return mVerticalSlider.sliderProgress
    }
}
