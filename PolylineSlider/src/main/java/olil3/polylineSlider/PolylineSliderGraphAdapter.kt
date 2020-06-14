package olil3.polylineSlider

import android.content.Context
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper

class PolylineSliderGraphAdapter(
    private val mConstraintLayout: PolylineSlider,
    private val mNumberOfDataPoints: Int,
    private val mSliderSpacing: Int,
    private val mSliderAlphaVal: Int,
    private val mThumbColorFilter: PorterDuffColorFilter,
    private val mSliderColorFilter: PorterDuffColorFilter,
    private val mSliderWrapperIDArray: IntArray,
    private val mContext: Context
) :
    RecyclerView.Adapter<PolylineSliderGraphAdapter.VerticalSeekBarObject>() {
    override fun getItemCount(): Int {
        return mNumberOfDataPoints
    }

    override fun onBindViewHolder(holder: VerticalSeekBarObject, position: Int) {
        mSliderWrapperIDArray[position] = holder.mSliderWrapper.id
        val mSlider = holder.mSliderWrapper.getChildAt(0) as VerticalSeekBar
        mSlider
            .setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    mConstraintLayout.updateSliderParams(mSlider.id, position)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalSeekBarObject {
        val mSliderWrapper = LayoutInflater.from(mContext)
            .inflate(R.layout.vertical_seek_bar_item, parent, false) as VerticalSeekBarWrapper
        mSliderWrapper.layoutParams =
            RecyclerView.LayoutParams(mSliderSpacing, RecyclerView.LayoutParams.MATCH_PARENT)
        mSliderWrapper.id = View.generateViewId()

        val mSlider = mSliderWrapper.getChildAt(0) as VerticalSeekBar
        mSlider.id = View.generateViewId()

        mSlider.progressDrawable.alpha = mSliderAlphaVal
        mSlider.thumb.colorFilter = mThumbColorFilter
        mSlider.progressDrawable.colorFilter = mSliderColorFilter
        return (VerticalSeekBarObject(mSliderWrapper))
    }

    override fun getItemId(position: Int): Long {
        return mSliderWrapperIDArray[position].toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class VerticalSeekBarObject(val mSliderWrapper: VerticalSeekBarWrapper) :
        RecyclerView.ViewHolder(mSliderWrapper)
}