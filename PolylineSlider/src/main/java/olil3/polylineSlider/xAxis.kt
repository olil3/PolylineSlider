package olil3.polylineSlider

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView

class xAxis(mContext: Context, var mNumberOfDataPoints: Int, val mUnits: String) :
    HorizontalScrollView(mContext) {
    var mLinearLayout: LinearLayout = LinearLayout(context)
    var mSliderSpacing: Int = 0

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
        isScrollbarFadingEnabled = false
        this.addView(mLinearLayout)
    }

    fun initializeBaseUi() {
        for (i in 0 until mNumberOfDataPoints) {
            val mTextBox = TextView(context)
            mTextBox.id = View.generateViewId()
            mTextBox.text = ((i + 1).toString() + mUnits)
            mTextBox.gravity = Gravity.CENTER

            val textBoxLinearParams =
                LinearLayout.LayoutParams(mSliderSpacing, 80)
            mLinearLayout.addView(mTextBox, textBoxLinearParams)
        }
    }
}