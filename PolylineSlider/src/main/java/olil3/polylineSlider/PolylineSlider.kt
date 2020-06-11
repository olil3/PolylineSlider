package olil3.polylineSlider

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper
import olil3.polylineSlider.utils.EPointF
import olil3.polylineSlider.utils.PolyBezierPathUtil
import kotlin.math.abs

class PolylineSlider : RelativeLayout {
    private var mPolylineSliderGraph: PolylineSliderGraph
    private var mNumberOfDataPoints = 0
    private var sliderAlphaValue: Int? = 0
    private var mThumbColor: Int = 0
    private lateinit var mSliderWrapperViewIDs: IntArray
    private lateinit var mSliderThumbColor: PorterDuffColorFilter
    private val mThumbCoordinateList: HashMap<Int, EPointF> = hashMapOf()
    private var ySliderThumbPos: Float = 0.0f
    private val bezierPathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var isBaseUIInitialized = false
    private var viewWidth = 0
    private var viewHeight = 0
    private lateinit var mScrollViewRelativeLayout: RelativeLayout

    constructor(mContext: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        mContext,
        attributeSet,
        defStyleAttr
    ) {
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
            mContext
        )
        this.addView(mPolylineSliderGraph)
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)
    constructor(mContext: Context) : this(mContext, null, 0)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (!isBaseUIInitialized) {
            viewHeight = abs(t - b)
            viewWidth = abs(r - l)

            mPolylineSliderGraph.initializeBaseUI()
            isBaseUIInitialized = true
            invalidate()
        }
    }

    private inner class PolylineSliderGraph(
        mContext: Context
    ) : HorizontalScrollView(mContext) {

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

        fun initializeBaseUI() {
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
                        ySliderThumbPos =
                            getThumbXYCoordinatesAsEPointF(mSlider).y // Get Y - element
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
                mListOfEPointFs.add(0, EPointF(0.0f, ySliderThumbPos))
                mListOfEPointFs.add(
                    EPointF(
                        this.computeHorizontalScrollRange().toFloat(),
                        ySliderThumbPos
                    )
                )

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
    }
}