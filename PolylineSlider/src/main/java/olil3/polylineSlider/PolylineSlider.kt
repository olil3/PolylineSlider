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
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs

class PolylineSlider : ConstraintLayout {
    private var mSliderFrameLayout: FrameLayout
    private var mXAxisFrameLayout: FrameLayout
    private var mNumberOfDataPoints = 0
    private var sliderAlphaValue: Int = 0
    private var mThumbColor: Int = 0
    private var mGradientColor: Int = 0
    private var mSliderSpacing: Int = 0
    private var isUIInitialized = false
    private var mViewWidth = 0
    private var mViewHeight = 0
    private var mTextViewID: IntArray

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        setWillNotDraw(true)
        isHorizontalScrollBarEnabled = true
        View.inflate(context, R.layout.polyline_slider, this)
        mSliderFrameLayout = findViewById(R.id.polyline_slider_graph_frame_layout)
        mXAxisFrameLayout = findViewById(R.id.polyline_x_axis_frame_layout)

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
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)
    constructor(mContext: Context) : this(mContext, null, 0)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!isUIInitialized) {
            mViewWidth = abs(right - left)
            mViewHeight = abs(top - bottom)
            mSliderSpacing = getSliderSpacing(mNumberOfDataPoints)
            objectInit()
            isUIInitialized = true
        }
    }

    private fun objectInit() {
        val mThumbColorFilter = PorterDuffColorFilter(mThumbColor, PorterDuff.Mode.SRC_ATOP)
        val mSliderColorFilter = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)

        val mPolylineSliderGraph = PolylineSliderGraph(
            context, mNumberOfDataPoints,
            mGradientColor, mSliderSpacing,
            this, sliderAlphaValue,
            mThumbColorFilter, mSliderColorFilter,
            IntArray(mNumberOfDataPoints)
        )
        mSliderFrameLayout.addView(
            mPolylineSliderGraph,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        val mXAxis = XAxis(
            context,
            mNumberOfDataPoints,
            mSliderSpacing,
            "Hrs",
            mTextViewID
        )

        mXAxisFrameLayout.addView(
            mXAxis,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        mXAxis.setLayout()
        mXAxis.setAdapter()

        mXAxis.post {
            mPolylineSliderGraph.setAdapter()
            mPolylineSliderGraph.setLayoutParams()
            mPolylineSliderGraph.post {
                mPolylineSliderGraph.initiatePostSequence()
            }
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
                mViewWidth / numberOfSlidersInView
            } else {
                mViewWidth / numberOfPoints
            }
        } else {
            mViewWidth / numberOfPoints
        }
    }

    internal fun performViewScroll(scrollingValue: Int) {
        (mXAxisFrameLayout.getChildAt(0) as XAxis).scrollBy(scrollingValue, 0)
    }
}
