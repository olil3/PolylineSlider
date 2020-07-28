package olil3.polylineSlider

import android.content.Context
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import olil3.polylineSlider.utils.EPointF
import olil3.polylineSlider.utils.VerticalSeekBarWrapper

internal class PolylineSliderGraph(
    mContext: Context,
    private val mDataClass: PolylineSliderProperties,
    private var mPolylineSlider: PolylineSlider,
    mSliderWrapperID: IntArray,
    private val mSliderSpacing: Int
) : RecyclerView(mContext) {

    private lateinit var mInitialEPointF: EPointF
    private lateinit var mEPointFXVal: FloatArray
    private lateinit var mEPointFYVal: FloatArray
    private var isLayout = false

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = PolylineSliderGraphAdapter(
            this,
            mDataClass,
            mSliderSpacing,
            mSliderWrapperID,
            context
        )
        mPolylineSlider.invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            isLayout = changed
        }
    }

    fun initiatePostSequence() {
        mEPointFYVal = FloatArray(mDataClass.mNumberOfDataPoints)
        mEPointFXVal = FloatArray(mDataClass.mNumberOfDataPoints)
        mInitialEPointF =
            ((this.getChildAt(0) as RelativeLayout).getChildAt(0) as VerticalSeekBarWrapper).getSliderCoordinates()

        for (addBasePoints in 0 until mDataClass.mNumberOfDataPoints) {
            mEPointFXVal[addBasePoints] = mInitialEPointF.x + (addBasePoints * mSliderSpacing)
            mEPointFYVal[addBasePoints] = mInitialEPointF.y
        }

        this.setOnScrollChangeListener { _, _, _, _, _ ->
            for (updateBasePoints in 0 until mDataClass.mNumberOfDataPoints) {
                mEPointFXVal[updateBasePoints] =
                    mInitialEPointF.x +
                        (updateBasePoints * mSliderSpacing) -
                        this.computeHorizontalScrollOffset()
            }
            mPolylineSlider.invalidate()
        }
        mPolylineSlider.invalidate()
    }

    fun updateSliderParams(mVerticalSlider: VerticalSeekBarWrapper, position: Int) {
        val yVal = mVerticalSlider.getSliderCoordinates().y
        mEPointFYVal[position] = yVal
        mPolylineSlider.invalidate()
    }

    fun getEPointFXArray(): FloatArray? {
        return if (this::mEPointFXVal.isInitialized) {
            mEPointFXVal
        } else {
            null
        }
    }

    fun getEPointFYArray(): FloatArray? {
        return if (this::mEPointFYVal.isInitialized) {
            mEPointFYVal
        } else {
            null
        }
    }

    fun isLayoutComplete(): Boolean {
        return isLayout
    }

    fun getInitialEPointF(): EPointF {
        return mInitialEPointF
    }
}
