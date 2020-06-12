package olil3.polylineSlider

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import android.widget.TextView

class xAxis(mContext: Context, private var mNumberOfDataPoints: Int, private val mUnits: String) :
    HorizontalScrollView(mContext) {
    private var mLinearLayout: RelativeLayout = RelativeLayout(context)
    var mSliderSpacing: Int = 0

    init {
        setWillNotDraw(true)
        this.addView(mLinearLayout)
    }

    fun initializeBaseUi() {
        var previousID = 0
        for (i in 0 until mNumberOfDataPoints) {
            val mTextBox = TextView(context)
            mTextBox.id = View.generateViewId()
            mTextBox.text = ((i + 1).toString() + mUnits)
            mTextBox.gravity = Gravity.CENTER_HORIZONTAL
            val textBoxLinearParams =
                RelativeLayout.LayoutParams(mSliderSpacing, ViewGroup.LayoutParams.MATCH_PARENT)

            if (i != 0) {
                textBoxLinearParams.addRule(RelativeLayout.RIGHT_OF, previousID)
                mLinearLayout.addView(mTextBox, textBoxLinearParams)
            } else {
                mLinearLayout.addView(mTextBox, textBoxLinearParams)
            }
            previousID = mTextBox.id
        }
    }
}