package olil3.polylineSlider

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper

class PolylineSlider : ConstraintLayout {
    private var mSliderRecyclerView: RecyclerView
    private var mXAxisLinearLayout: LinearLayout
    private var mNumberOfDataPoints = 0
    private var sliderAlphaValue: Int = 0
    private var mThumbColor: Int = 0
    private var mGradientColor: Int = 0
    private var mSliderSpacing: Int = 0
    private lateinit var mInitialEPointF: EPointF
    private lateinit var mEPointFXVal: FloatArray
    private lateinit var mEPointFYVal: FloatArray
    private val mBezierUtil = PolyBezierPathUtil()
    private val mPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var mRecyclerViewParams: IntArray
    private val mGradientPath = Path()

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
        setWillNotDraw(false)
        View.inflate(context, R.layout.polyline_slider, this)
        mSliderRecyclerView = findViewById(R.id.slider_graph)
        mXAxisLinearLayout = findViewById(R.id.slider_x_axis)

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
        mPathPaint.style = Paint.Style.STROKE
        mPathPaint.strokeWidth = 5f
        mPathPaint.color = mThumbColor
        objectInit()
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)

    private fun objectInit() {
        val recyclerViewLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val mThumbColorFilter = PorterDuffColorFilter(mThumbColor, PorterDuff.Mode.SRC_ATOP)
        val mSliderColorFilter = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)
        val mSliderWrapperID = IntArray(mNumberOfDataPoints)

        mEPointFXVal = FloatArray(mNumberOfDataPoints)
        mEPointFYVal = FloatArray(mNumberOfDataPoints)
        mSliderSpacing = 200

        val mVerticalSliderAdapter = PolylineSliderGraphAdapter(
            this,
            mNumberOfDataPoints, mSliderSpacing,
            sliderAlphaValue, mThumbColorFilter,
            mSliderColorFilter, mSliderWrapperID, context
        )

        mSliderRecyclerView.layoutManager = recyclerViewLayoutManager
        mSliderRecyclerView.adapter = mVerticalSliderAdapter

        mSliderRecyclerView.post {
            mRecyclerViewParams = intArrayOf(mSliderRecyclerView.width, mSliderRecyclerView.height)
            mGradientPaint.shader = LinearGradient(
                0f,
                0f,
                0f,
                mRecyclerViewParams[1].toFloat(),
                mGradientColor,
                Color.TRANSPARENT,
                Shader.TileMode.MIRROR
            )
            mInitialEPointF = getThumbXYCoordinatesAsEPointF(
                (mSliderRecyclerView.getChildAt(0) as VerticalSeekBarWrapper).getChildAt(0) as VerticalSeekBar
            )

            for (addBasePoints in 0 until mNumberOfDataPoints) {
                mEPointFXVal[addBasePoints] = mInitialEPointF.x + (addBasePoints * mSliderSpacing)
                mEPointFYVal[addBasePoints] = mInitialEPointF.y
            }

            mSliderRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
                for (updateBasePoints in 0 until mNumberOfDataPoints) {
                    mEPointFXVal[updateBasePoints] =
                        mInitialEPointF.x +
                                (updateBasePoints * mSliderSpacing) -
                                mSliderRecyclerView.computeHorizontalScrollOffset()
                }
                invalidate()
            }
            invalidate()
        }
    }

    private fun getThumbXYCoordinatesAsEPointF(seekBarToFind: VerticalSeekBar): EPointF {
        val seekBarWrapper = seekBarToFind.parent as VerticalSeekBarWrapper
        val seekBarThumbBounds = seekBarToFind.thumb.bounds
        val xPos: Float =
            seekBarWrapper.left + seekBarThumbBounds.exactCenterY() + ((seekBarWrapper.width - (seekBarToFind.paddingLeft * 1.1f)) / 2)
        val yPos: Float =
            seekBarWrapper.bottom - seekBarThumbBounds.exactCenterX() - (seekBarThumbBounds.height() * 0.4f)

        return EPointF(xPos, yPos)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        try {
            val pathToDraw = mBezierUtil.computePathThroughKnots(
                mEPointFXVal,
                mEPointFYVal,
                mInitialEPointF.y,
                this.computeHorizontalScrollRange(),
                mGradientPath
            )
            canvas?.clipRect(
                this.computeHorizontalScrollOffset(),
                0,
                mRecyclerViewParams[0] + this.computeHorizontalScrollOffset(),
                mRecyclerViewParams[1]
            )
            mGradientPath.lineTo(
                this.computeHorizontalScrollRange().toFloat(),
                mRecyclerViewParams[1].toFloat()
            )
            mGradientPath.lineTo(0.0f, mRecyclerViewParams[1].toFloat())
            mGradientPath.lineTo(0.0f, mInitialEPointF.y)
            canvas?.drawPath(mGradientPath, mGradientPaint)
            canvas?.drawPath(pathToDraw, mPathPaint)
        } catch (e: UninitializedPropertyAccessException) {
        }
    }

    internal fun updateSliderParams(sliderID: Int, position: Int) {
        val yVal = getThumbXYCoordinatesAsEPointF(findViewById(sliderID)).y
        mEPointFYVal[position] = yVal
        invalidate()
    }
}