package olil3.polylineSlider.uiComponents

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class YAxisAdapter(
    private val mContext: Context,
    private val mNumberOfTextBoxes: Int,
    private val mTextBoxSpacing: Int,
    private val mUnit: String,
    private val initialValue: Int,
    private val mTextBoxViewIDs: IntArray
) : RecyclerView.Adapter<YAxisAdapter.YAxisValueTextViewHolder>() {
    class YAxisValueTextViewHolder(val mValueTextView: TextView) :
        RecyclerView.ViewHolder(mValueTextView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YAxisValueTextViewHolder {
        val mValueTextView = TextView(mContext)
        mValueTextView.id = View.generateViewId()
        mValueTextView.textSize = 12f
        mValueTextView.gravity = Gravity.CENTER
        mValueTextView.layoutParams =
            ViewGroup.LayoutParams(mTextBoxSpacing, ViewGroup.LayoutParams.MATCH_PARENT)
        return YAxisValueTextViewHolder(mValueTextView)
    }

    override fun getItemCount(): Int {
        return mNumberOfTextBoxes
    }

    override fun onBindViewHolder(holder: YAxisValueTextViewHolder, position: Int) {
        mTextBoxViewIDs[position] = holder.mValueTextView.id
        holder.mValueTextView.text = ((initialValue).toString() + mUnit)
    }

    override fun getItemId(position: Int): Long {
        return mTextBoxViewIDs[position].toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
