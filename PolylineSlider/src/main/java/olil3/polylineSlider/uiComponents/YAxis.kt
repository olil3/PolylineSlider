package olil3.polylineSlider.uiComponents

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.isVisible

class YAxis(mContext: Context, attributeSet: AttributeSet): FrameLayout(mContext, attributeSet) {
    private var mPercentageView: TextView = TextView(context)
    private var mProgressBar: VerticalSlider = VerticalSlider(context)
    private var isInitialized = false

    var visibility: Boolean = false
        set(value) {
            if (value) {
                mPercentageView.visibility = View.VISIBLE
                mProgressBar.sliderAlpha = 255
            } else {
                mPercentageView.visibility = View.INVISIBLE
                mProgressBar.sliderAlpha = 0
            }
            field = value
        }

    private fun mInit() {
        visibility = false
        setWillNotDraw(true)

        val mProgressBarLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val mPercentageViewLayoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP)
        this.addView(mPercentageView, mPercentageViewLayoutParams)
        this.addView(mProgressBar, mProgressBarLayoutParams)

        mPercentageView.textSize = 13f
        mPercentageView.gravity = Gravity.CENTER_HORIZONTAL
        mPercentageView.text = ((mProgressBar.getSeekBarObject().progress).toString() + "%")

        mProgressBar.thumbAlpha = 0
        mProgressBar.thumbColor = PorterDuffColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP)
        mProgressBar.getSeekBarObject().setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mPercentageView.text = ((mProgressBar.getSeekBarObject().progress).toString() + "%")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                // Do nothing
            }
        })
    }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            if (!isInitialized) {
                mInit()
                isInitialized = true
            }
            mProgressBar.layoutParams.width = measuredWidth
            mProgressBar.layoutParams.height = measuredHeight
            mProgressBar.requestLayout()
            mProgressBar.post {
                mPercentageView.layoutParams.width = measuredWidth
                mPercentageView.requestLayout()
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    fun setSliderProgress(newSliderProgress: Int) {
        mProgressBar.getSeekBarObject().progress = newSliderProgress
    }
}
