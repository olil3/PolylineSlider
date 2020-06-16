package olil3.polylineSlider

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal class XAxis(
    mContext: Context,
    private val mNumberOfTextBoxes: Int,
    private val mTextBoxSpacing: Int,
    private val xAxisUnit: String,
    private val mTextBoxID: IntArray
) : RecyclerView(mContext) {

    init {
        setWillNotDraw(false)
        overScrollMode = View.OVER_SCROLL_NEVER
    }

    fun setLayout() {
        val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager = mLayoutManager
    }

    fun setAdapter() {
        adapter = XAxisAdapter(context, mNumberOfTextBoxes, mTextBoxSpacing, xAxisUnit, mTextBoxID)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}
