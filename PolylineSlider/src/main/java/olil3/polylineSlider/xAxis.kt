package olil3.polylineSlider

import android.content.Context
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout

class xAxis(mContext: Context, var mNumberOfDataPoints: Int, val mUnits: String) :
    HorizontalScrollView(mContext) {
    var mLinearLayout: LinearLayout = LinearLayout(context)
    var mSliderSpacing: Int = 0

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
        isScrollbarFadingEnabled = false
        this.addView(mLinearLayout)
    }
}