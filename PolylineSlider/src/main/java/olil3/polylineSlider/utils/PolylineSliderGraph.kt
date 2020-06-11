package olil3.polylineSlider.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.RelativeLayout.RIGHT_OF
import android.widget.SeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper
import olil3.polylineSlider.R
import kotlin.math.abs

@SuppressLint("ViewConstructor")
class PolylineSliderGraph(
    mNumberOfDataPts: Int,
    sliderAlphaVal: Int?,
    mThumbClr: Int,
    mContext: Context
) : HorizontalScrollView(mContext) {

    private lateinit var mSliderWrapperViewIDs: IntArray
    private var mNumberOfDataPoints: Int = mNumberOfDataPts
    private var mThumbColor: Int = mThumbClr
    private lateinit var mSliderThumbColor: PorterDuffColorFilter
    private var sliderAlphaValue: Int? = sliderAlphaVal
    private val mThumbCoordinateList: HashMap<Int, EPointF> = hashMapOf()
    private var ySliderThumbPos: Float = 0.0f
    private val bezierPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isBaseUIInitialized = false
    private var viewWidth = 0
    private var viewHeight = 0
    private lateinit var mScrollViewRelativeLayout: RelativeLayout

    init {
        isSmoothScrollingEnabled = true
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

        mSliderThumbColor = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)
        mSliderWrapperViewIDs = IntArray(mNumberOfDataPoints)
    }

    private fun initializeBaseUI() {
        mScrollViewRelativeLayout =
            findViewById(R.id.polylineRelativeLayout)

        for (sliderWrapperPos in 0 until mNumberOfDataPoints) {
            val mSlider = VerticalSeekBar(context)
            val mSliderWrapper = VerticalSeekBarWrapper(context)

            mSliderWrapper.id = View.generateViewId()
            mSliderWrapperViewIDs[sliderWrapperPos] = mSliderWrapper.id

            mSlider.rotationAngle = VerticalSeekBar.ROTATION_ANGLE_CW_270
            mSlider.max = 100
            mSlider.progress = 50
            mSlider.splitTrack = false
            mSlider.progressDrawable.alpha = sliderAlphaValue!!
            mSlider.thumb.colorFilter = mSliderThumbColor
            mSlider.progressDrawable.colorFilter = mSliderThumbColor

            mSlider.post {
                mThumbCoordinateList[mSliderWrapper.id] = getThumbXYCoordinatesAsEPointF(mSlider)
            }

            mSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    mThumbCoordinateList[mSliderWrapper.id] =
                        getThumbXYCoordinatesAsEPointF(mSlider)
                    invalidate()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })

            mSliderWrapper.addView(mSlider)

            val sliderPositioningParams =
                RelativeLayout.LayoutParams(150, ViewGroup.LayoutParams.MATCH_PARENT)
            if (sliderWrapperPos != 0) {
                sliderPositioningParams.addRule(
                    RIGHT_OF,
                    mSliderWrapperViewIDs[sliderWrapperPos - 1]
                )
                mScrollViewRelativeLayout.addView(mSliderWrapper, sliderPositioningParams)

            } else {
                mScrollViewRelativeLayout.addView(mSliderWrapper, sliderPositioningParams)
                mSliderWrapper.post {
                    ySliderThumbPos = getThumbXYCoordinatesAsEPointF(mSlider).y // Get Y - element
                }
            }
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

    private fun getBezierPathForThumbs(): Path? {
        return if (mNumberOfDataPoints == mThumbCoordinateList.entries.size) {
            val mListOfEPointFs = arrayListOf<EPointF>()

            for (i in 0 until mNumberOfDataPoints) {
                mListOfEPointFs.add(mThumbCoordinateList[mSliderWrapperViewIDs[i]]!!)
            }

            PolyBezierPathUtil().computePathThroughKnots(mListOfEPointFs)
        } else {
            null
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val pathToDraw = getBezierPathForThumbs()
        if (pathToDraw != null) {
            canvas?.drawPath(pathToDraw, bezierPathPaint)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!isBaseUIInitialized) {
            viewHeight = abs(t - b)
            viewWidth = abs(r - l)
            initializeBaseUI()
            isBaseUIInitialized = true
            invalidate()
        }
    }
}