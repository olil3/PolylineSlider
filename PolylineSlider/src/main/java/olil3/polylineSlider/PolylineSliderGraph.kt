package olil3.polylineSlider

import android.content.Context
import android.graphics.*
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper

internal class PolylineSliderGraph(
    mContext: Context,
    mNumberOfDtPts: Int,
    mSliderAlphaVal: Int,
    mThumbClr: Int,
    mGradientCol: Int

) : HorizontalScrollView(mContext) {
    lateinit var mSliderThumbColor: PorterDuffColorFilter
    val mThumbCoordinateList: HashMap<Int, EPointF> = hashMapOf()
    var ySliderThumbPos: Float = 0.0f
    private val bezierPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGradientPath = Path()
    private val mGradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    lateinit var mScrollViewLinearLayout: LinearLayout
    private var mNumberOfDataPoints = 0
    private var sliderAlphaValue: Int = 0
    private var mThumbColor: Int = 0
    var viewHeight = 0
    var viewWidth = 0
    var mSliderSpacingWidth: Int = 0
    private var mGradientColor: Int = 0
    lateinit var mSliderWrapperViewIDs: IntArray

    init {
        mNumberOfDataPoints = mNumberOfDtPts
        sliderAlphaValue = mSliderAlphaVal
        mThumbColor = mThumbClr
        mGradientColor = mGradientCol
        objectInit(mContext)
    }

    private fun objectInit(mContext: Context) {
        View.inflate(
            mContext,
            R.layout.polyline_slider_graph, this
        )

        bezierPathPaint.color = mThumbColor
        bezierPathPaint.style = Paint.Style.STROKE
        bezierPathPaint.strokeWidth = 5f
        mScrollViewLinearLayout =
            findViewById(R.id.polylineRelativeLayout)

        mSliderThumbColor = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)
        mSliderWrapperViewIDs = IntArray(mNumberOfDataPoints)
        isHorizontalScrollBarEnabled = false
        setOnTouchListener { v, _ ->
            v.performClick()
            true
        }
    }

    fun getThumbXYCoordinatesAsEPointF(seekBarToFind: VerticalSeekBar): EPointF {
        val seekBarWrapper = seekBarToFind.parent as VerticalSeekBarWrapper
        val seekBarThumbBounds = seekBarToFind.thumb.bounds
        val xPos: Float =
            seekBarWrapper.left + seekBarThumbBounds.exactCenterY() + ((seekBarWrapper.width - (seekBarToFind.paddingLeft * 1.1f)) / 2)
        val yPos: Float =
            seekBarWrapper.bottom - seekBarThumbBounds.exactCenterX() - (seekBarThumbBounds.height() * 0.4f)

        return EPointF(xPos, yPos)
    }

    private fun getBezierPathForThumbs(): Path? {
        return if (mNumberOfDataPoints == mThumbCoordinateList.entries.size) {
            val mListOfEPointFs = arrayListOf<EPointF>()
            mListOfEPointFs.addAll(mThumbCoordinateList.values)
            mListOfEPointFs.add(
                0,
                EPointF(0.0f, ySliderThumbPos)
            )
            mListOfEPointFs.add(
                EPointF(
                    this.computeHorizontalScrollRange().toFloat(),
                    ySliderThumbPos
                )
            )
            val pathToReturn = PolyBezierPathUtil().computePathThroughKnots(mListOfEPointFs)
            mGradientPath.set(pathToReturn)
            mGradientPaint.shader = LinearGradient(
                0f,
                0f,
                0f,
                viewHeight.toFloat(),
                mGradientColor,
                Color.TRANSPARENT,
                Shader.TileMode.MIRROR
            )
            pathToReturn
        } else {
            null
        }
    }

    @Synchronized
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val pathToDraw = getBezierPathForThumbs()
        if (pathToDraw != null) {
            mGradientPath.lineTo(
                this.computeHorizontalScrollRange().toFloat(),
                viewHeight.toFloat()
            )
            canvas?.clipRect(this.scrollX, 0, viewWidth + this.scrollX, viewHeight)
            mGradientPath.lineTo(0.0f, viewHeight.toFloat())
            mGradientPath.lineTo(0.0f, ySliderThumbPos)
            canvas?.drawPath(mGradientPath, mGradientPaint)
            canvas?.drawPath(pathToDraw, bezierPathPaint)
        }
    }
}