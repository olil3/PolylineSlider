package olil3.polylineSlider.uiComponents

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import olil3.polylineSlider.X_AXIS_TYPE
import olil3.polylineSlider.Y_AXIS_TYPE

internal class Axis : RecyclerView {
    private var mNumberOfTextBoxes: Int = 0
    private var mTextBoxSpacing: Int = 0
    private lateinit var mAxisUnit: String
    private lateinit var mTextBoxID: IntArray
    private var mAxisType: Int = 0

    constructor(mContext: Context, attributeSet: AttributeSet?, defAttributeStyle: Int) : super(
        mContext,
        attributeSet,
        defAttributeStyle
    ) {
        setWillNotDraw(false)
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    constructor(mContext: Context, attributeSet: AttributeSet?) : this(mContext, attributeSet, 0)
    constructor(mContext: Context) : this(mContext, null, 0)

    fun setLayout() {
        val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager = mLayoutManager
    }

    fun setAdapter(mType: Int, mInitialValue: Int) {
        if (!(mType == X_AXIS_TYPE || mType == Y_AXIS_TYPE)) {
            throw IllegalArgumentException("Invalid AXIS TYPE!")
        }
        adapter = if (mType == X_AXIS_TYPE) {
            mAxisType = X_AXIS_TYPE
            XAxisAdapter(
                context,
                mNumberOfTextBoxes,
                mTextBoxSpacing,
                mAxisUnit,
                mTextBoxID
            )
        } else {
            mAxisType = Y_AXIS_TYPE
            YAxisAdapter(
                context,
                mNumberOfTextBoxes,
                mTextBoxSpacing,
                mAxisUnit,
                mInitialValue,
                mTextBoxID
            )
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    fun setNumberOfItems(mNumberOfItems: Int) {
        mNumberOfTextBoxes = mNumberOfItems
    }

    fun setUnit(mUnit: String) {
        mAxisUnit = mUnit
    }

    fun setItemViewIDArray(mViewIDArray: IntArray) {
        mTextBoxID = mViewIDArray
    }

    fun setItemSpacing(mItemSpacing: Int) {
        mTextBoxSpacing = mItemSpacing
    }

    fun changeYAxisProgress(position: Int, progress: Int) {
        if (adapter is YAxisAdapter) {
            findViewById<TextView>((adapter as YAxisAdapter).getItemId(position).toInt()).text =
                ((progress).toString() + mAxisUnit)
        }
    }
}
