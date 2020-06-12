package olil3.polylineSlider

import android.content.Context
import android.graphics.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper

internal class PolylineSliderGraph(
    mContext: Context,
    mNumberOfDtPts: Int,
    mSliderAlphaVal: Int,
    mThumbClr: Int,
    mGradientCol: Int

) : HorizontalScrollView(mContext) {
    private lateinit var mSliderThumbColor: PorterDuffColorFilter
    private val mThumbCoordinateList: HashMap<Int, EPointF> = hashMapOf()
    private var ySliderThumbPos: Float = 0.0f
    private val bezierPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mGradientPath = Path()
    private val mGradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var mScrollViewRelativeLayout: RelativeLayout
    private var mNumberOfDataPoints = 0
    private var sliderAlphaValue: Int = 0
    private var mThumbColor: Int = 0
    var viewHeight = 0
    var viewWidth = 0
    var mSliderSpacingWidth: Int = 0
    private var mGradientColor: Int = 0
    private lateinit var mSliderWrapperViewIDs: IntArray

    init {
        isSmoothScrollingEnabled = true
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

        mSliderThumbColor = PorterDuffColorFilter(Color.MAGENTA, PorterDuff.Mode.SRC_ATOP)
        mSliderWrapperViewIDs = IntArray(mNumberOfDataPoints)
    }

    fun initializeBaseUI() {
        mScrollViewRelativeLayout =
            findViewById(R.id.polylineRelativeLayout)
        var previousRelID = 0

        for (sliderWrapperPos in 0 until mNumberOfDataPoints) {
            val mSlider = VerticalSeekBar(context)
            val mSliderWrapper = VerticalSeekBarWrapper(context)
            val mSliderRelativeLayout = RelativeLayout(context)
            val mTextBox = TextView(context)

            mSliderRelativeLayout.id = View.generateViewId()
            mTextBox.text = ((sliderWrapperPos + 1).toString() + "Hrs")
            mTextBox.gravity = Gravity.CENTER_HORIZONTAL
            mTextBox.textSize = 15f

            mSliderWrapper.id = View.generateViewId()
            mSliderWrapperViewIDs[sliderWrapperPos] = mSliderWrapper.id

            mSlider.rotationAngle = VerticalSeekBar.ROTATION_ANGLE_CW_270
            mSlider.max = 100
            mSlider.progress = 50
            mSlider.splitTrack = false
            mSlider.progressDrawable.alpha = sliderAlphaValue
            mSlider.thumb.colorFilter = mSliderThumbColor
            mSlider.progressDrawable.colorFilter = mSliderThumbColor

            mSlider.post {
                mThumbCoordinateList[mSliderWrapper.id] =
                    getThumbXYCoordinatesAsEPointF(mSlider)
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

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    mTextBox.typeface = Typeface.DEFAULT_BOLD
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    mTextBox.typeface = Typeface.DEFAULT
                }
            })

            mSliderWrapper.tag = mSlider
            mSlider.tag = mSliderRelativeLayout
            mSliderWrapper.addView(mSlider)
            val relParams = RelativeLayout.LayoutParams(viewWidth, viewHeight - 50)
            mSliderRelativeLayout.addView(mSliderWrapper, relParams)
            //mTextBox.setPadding(0, 100, 0, 0)
            val textRelParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textRelParams.addRule(RelativeLayout.BELOW, mSliderWrapper.id)
            mSliderRelativeLayout.addView(mTextBox, textRelParams)

            val sliderPositioningParams =
                RelativeLayout.LayoutParams(
                    mSliderSpacingWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            if (sliderWrapperPos != 0) {
                sliderPositioningParams.addRule(
                    RelativeLayout.RIGHT_OF,
                    previousRelID
                )
                mScrollViewRelativeLayout.addView(mSliderRelativeLayout, sliderPositioningParams)

            } else {
                mScrollViewRelativeLayout.addView(mSliderRelativeLayout, sliderPositioningParams)
                mSliderWrapper.post {
                    ySliderThumbPos =
                        getThumbXYCoordinatesAsEPointF(mSlider).y // Get Y - element
                }
            }
            previousRelID = mSliderRelativeLayout.id
        }
        invalidate()
    }

    private fun getThumbXYCoordinatesAsEPointF(seekBarToFind: VerticalSeekBar): EPointF {
        val seekBarWrapper = seekBarToFind.parent as VerticalSeekBarWrapper
        val seekBarRelativeLayout = seekBarToFind.tag as RelativeLayout
        val seekBarThumbBounds = seekBarToFind.thumb.bounds
        val xPos: Float =
            seekBarRelativeLayout.left + seekBarWrapper.left + seekBarThumbBounds.exactCenterY() + ((seekBarWrapper.width - (seekBarToFind.paddingLeft * 1.1f)) / 2)
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

    override fun onDraw(canvas: Canvas?) {
        val pathToDraw = getBezierPathForThumbs()
        if (pathToDraw != null) {
            mGradientPath.lineTo(
                this.computeHorizontalScrollRange().toFloat(),
                viewHeight.toFloat()
            )
            mGradientPath.lineTo(0.0f, viewHeight.toFloat())
            mGradientPath.lineTo(0.0f, ySliderThumbPos)

            canvas?.clipRect(
                0.0f + this.scrollX,
                0.0f,
                viewWidth.toFloat() + this.scrollX,
                (viewHeight).toFloat()
            )
            canvas?.drawPath(mGradientPath, mGradientPaint)
            canvas?.drawPath(pathToDraw, bezierPathPaint)
            super.onDraw(canvas)
        }
    }
}