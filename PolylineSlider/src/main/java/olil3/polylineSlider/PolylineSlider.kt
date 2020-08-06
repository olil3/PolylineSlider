package olil3.polylineSlider

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import olil3.polylineSlider.utils.PolyBezierPathUtil

class PolylineSlider : ConstraintLayout { // Todo: Add save state functionality
    private var mDataClass: PolylineSliderProperties

    private lateinit var mSliderComponent: PolylineSliderGraph

    private var mSliderSpacing: Int = 0
    private val axisHeight: Float = context.resources.getDimension(R.dimen.axis_text_view_height)

    private var isUIInitialized: Boolean = false
    private var mVerticalSliderIDs: IntArray

    private val mGradientPath: Path = Path()
    private val mGradientPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mBezierPathUtil: PolyBezierPathUtil = PolyBezierPathUtil()
    private val mPathPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)
    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        View.inflate(context, R.layout.polyline_slider, this)
        var mNumberOfDataPoints = 1
        var sliderAlphaValue = 0
        var mThumbColor: Int? = null
        var mSliderColor: Int? = null
        var mGradientColor: Int? = null
        var mXAxisUnit: String? = null
        var mXAxisValues: Array<String>? = null
        var mYAxisUnit: String? = null
        var mYAxisMaxValue = 100f
        var mYAxisMinValue = 0f
        var mYAxisInitialValue = 50f

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
                mSliderColor =
                    attributes.getInt(R.styleable.PolylineSlider_slider_color, Color.MAGENTA)
                mGradientColor =
                    attributes.getColor(
                        R.styleable.PolylineSlider_gradient_color, Color.rgb(238, 130, 238)
                    )
                mXAxisUnit =
                    if (attributes.getString(R.styleable.PolylineSlider_x_axis_unit) == null) {
                        ""
                    } else {
                        attributes.getString(R.styleable.PolylineSlider_x_axis_unit).toString()
                    }
                mXAxisValues =
                    if (attributes.getTextArray(R.styleable.PolylineSlider_x_axis_custom_values) != null) {
                        Array(attributes.getTextArray(R.styleable.PolylineSlider_x_axis_custom_values).size)
                        { charSequence -> attributes.getTextArray(R.styleable.PolylineSlider_x_axis_custom_values)[charSequence].toString() }
                    } else {
                        null
                    }
                mYAxisUnit =
                    if (attributes.getString(R.styleable.PolylineSlider_y_axis_unit) == null) {
                        ""
                    } else {
                        attributes.getString(R.styleable.PolylineSlider_y_axis_unit).toString()
                    }
                mYAxisMaxValue =
                    attributes.getFloat(R.styleable.PolylineSlider_y_axis_max_value, 100f)
                mYAxisMinValue =
                    attributes.getFloat(R.styleable.PolylineSlider_y_axis_min_value, 0f)
                mYAxisInitialValue =
                    attributes.getFloat(R.styleable.PolylineSlider_y_axis_initial_value, 50f)
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
        mDataClass =
            PolylineSliderProperties(
                mNumberOfDataPoints,
                sliderAlphaValue == 255,
                mThumbColor,
                mSliderColor,
                mGradientColor,
                mXAxisUnit,
                mXAxisValues,
                mYAxisUnit,
                mYAxisMinValue,
                mYAxisMaxValue,
                mYAxisInitialValue
            )
        mVerticalSliderIDs = IntArray(mNumberOfDataPoints)
    }

    constructor(mContext: Context, mPolylineSliderProperties: PolylineSliderProperties) : super(
        mContext,
        null,
        0
    ) { // Inflate view
        mDataClass = mPolylineSliderProperties
        mVerticalSliderIDs = IntArray(mDataClass.mNumberOfDataPoints)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mSliderSpacing = getSliderSpacing(mDataClass.mNumberOfDataPoints)
            if (!isUIInitialized) {
                mSliderSpacing = getSliderSpacing(mDataClass.mNumberOfDataPoints)
                objectInit()
                isUIInitialized = true
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.save()
        canvas?.clipRect(
            Rect(
                0, axisHeight.toInt(), measuredWidth,
                (measuredHeight - axisHeight).toInt()
            )
        )
        drawPolyLine(canvas)
        canvas?.restore()
        canvas?.save()
        canvas?.clipRect(Rect(0, 0, measuredWidth, measuredHeight))
        super.dispatchDraw(canvas)
        canvas?.restore()
    }

    private fun drawPolyLine(canvas: Canvas?) {
        try {
            if (mSliderComponent.isLayoutComplete()) {
                mPathPaint.style = Paint.Style.STROKE
                mPathPaint.strokeWidth = 5f
                mPathPaint.color = Color.MAGENTA

                val pathToDraw = mBezierPathUtil.computePathThroughKnots(
                    mSliderComponent.getEPointFXArray()!!,
                    mSliderComponent.getEPointFYArray()!!,
                    mSliderComponent.getInitialEPointF().y,
                    mSliderComponent.computeHorizontalScrollRange(),
                    mSliderComponent.computeHorizontalScrollOffset(),
                    mGradientPath
                )
                mGradientPath.lineTo(
                    mSliderComponent.computeHorizontalScrollRange().toFloat(),
                    measuredHeight.toFloat()
                )
                mGradientPath.lineTo(0.0f, measuredHeight.toFloat())
                mGradientPath.lineTo(0.0f, mSliderComponent.getInitialEPointF().y)
                canvas?.drawPath(mGradientPath, mGradientPaint)
                canvas?.drawPath(pathToDraw, mPathPaint)
            }
        } catch (e: Exception) {
        }
    }

    private fun objectInit() {
        mGradientPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            measuredHeight.toFloat(),
            if (mDataClass.useDefaultGradientColor) mDataClass.DEFAULT_GRADIENT_COLOR else mDataClass.mGradientColor!!, // check boolean
            Color.TRANSPARENT,
            Shader.TileMode.MIRROR
        )

        mSliderComponent = PolylineSliderGraph(
            context,
            mDataClass,
            this,
            mVerticalSliderIDs,
            mSliderSpacing
        )
        findViewById<FrameLayout>(R.id.polyline_slider_graph_frame_layout).addView(
            mSliderComponent,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mSliderComponent.post {
            mSliderComponent.initiatePostSequence()
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

    fun getSliderProgressAsPercentage(): MutableMap<String, Int> {
        return mSliderComponent.getSliderProgressAsPercentage()
    }

    fun getSliderProgressAsValue(): MutableMap<String, Float> {
        return mSliderComponent.getSliderProgressAsValue()
    }
}
